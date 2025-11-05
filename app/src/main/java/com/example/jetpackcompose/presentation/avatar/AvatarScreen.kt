package com.example.jetpackcompose.presentation.avatar

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*

@Composable
fun AvatarScreen(
    memberId: Long,
    memberName: String,
    onFinished: (updatedMemberId: Long, oldOwnerMemberId: Long?) -> Unit
) {
    val ctx = LocalContext.current
    val vm: AvatarViewModel = viewModel(factory = AvatarViewModel.provideFactory(ctx))
    val ui by vm.ui.collectAsState()

    // 整個 avatar 功能要不要顯示
    var showDialog by remember { mutableStateOf(true) }

    // 本地先存一張「要拿來預覽的圖」，避免選本地圖時閃一下輸入框
    var previewImage by remember { mutableStateOf<Bitmap?>(null) }

    // VM 若生成了新圖，就同步到本地預覽
    LaunchedEffect(ui.image) {
        if (ui.image != null) {
            previewImage = ui.image
        }
    }

    if (!showDialog) return

    // ① 還在看「最近圖片」這一頁
    if (ui.showPreviousPicker) {
        PreviousImagesFullscreen(
            images = ui.previousImages,
            onSelect = { item ->
                // 先讓畫面這邊就有圖可以預覽
                previewImage = item.bitmap
                // 再讓 VM 知道你選了哪張
                vm.onPreviousPicked(item)
            },
            onClose = vm::dismissPreviousPicker
        )
        return
    }

    // ② 有預覽圖 → 顯示「Use this image?」
    if (previewImage != null) {
        AlertDialog(
            onDismissRequest = { previewImage = null },
            title = { Text("Use this image?") },
            text = {
                Image(
                    bitmap = previewImage!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentScale = ContentScale.Crop
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    // 一按使用就先把畫面收掉，避免閃回輸入框
                    showDialog = false

                    val pickedOldImageId = ui.selectedAvatarId
                    if (pickedOldImageId != null) {
                        // 舊圖 → 要讓 VM 去處理「換主人」
                        vm.confirmUseImageFromPreview(memberId) { oldOwnerId ->
                            onFinished(memberId, oldOwnerId)
                        }
                    } else {
                        // 新生成的 → 直接完成
                        onFinished(memberId, null)
                    }

                    previewImage = null
                }) {
                    Text("Use this image")
                }
            },
            dismissButton = {
                TextButton(onClick = { previewImage = null }) {
                    Text("Generate again")
                }
            }
        )
        return
    }

    // ③ 一般輸入 Dialog（已經整合成一個 AvatarContent 了）
    AvatarContent(
        memberName = memberName,
        fruit = ui.fruit,
        animal = ui.animal,
        loading = ui.loading,
        error = ui.error,
        onFruitChange = vm::onFruitChange,
        onAnimalChange = vm::onAnimalChange,
        onGenerateClick = { vm.generateImage(memberId) },  // 生成新圖
        onUsePreviousClick = vm::onUsePreviousClick,       // 打開「最近圖片」
        onDismiss = {
            showDialog = false
            onFinished(memberId, null)
        }
    )
}