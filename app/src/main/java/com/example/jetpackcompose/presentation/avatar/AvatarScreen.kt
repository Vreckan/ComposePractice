package com.example.jetpackcompose.presentation.avatar

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AvatarScreen(
    memberId: Long,
    memberName: String,
    onFinished: (updatedMemberId: Long, oldOwnerMemberId: Long?) -> Unit
) {
    val ctx = LocalContext.current
    val vm: AvatarViewModel = viewModel(factory = AvatarViewModel.provideFactory(ctx))
    val ui by vm.ui.collectAsState()

    var showDialog by remember { mutableStateOf(true) }

    if (!showDialog) return

    // ① 顯示「最近圖片」
    if (ui.showPreviousPicker) {
        PreviousImagesFullscreen(
            images = ui.previousImages,
            onSelect = { item ->
                vm.onPreviousPicked(item)
            },
            onClose = vm::dismissPreviousPicker,

        )
        return
    }

    // ② 有預覽圖（無論是生成還是舊圖）
    if (ui.image != null) {
        GeneratedPreviewDialog(
            image = ui.image!!,
            onUse = {
                val pickedOldImageId = ui.selectedAvatarId
                if (pickedOldImageId != null) {
                    // 舊圖 → 要讓 VM 去 rebind
                    vm.confirmUseImageFromPreview(memberId) { oldOwnerId ->
                        onFinished(memberId, oldOwnerId)
                    }
                }
                else {
                    val newAvatarId = ui.generatedAvatarId
                    if (newAvatarId != null) {
                        vm.bindGeneratedAvatarToMember(newAvatarId, memberId) { oldOwnerId ->
                            onFinished(memberId, oldOwnerId)
                        }
                    } else {
                        onFinished(memberId, null)
                    }
                }
                vm.clearPreview()  // ✅ 清除預覽狀態
                showDialog = false
            },
            onRegenerate = {
                vm.clearPreview()  // ✅ 使用者選擇「Generate again」時清除
            }
        )
        return
    }

    // ③ 預設：顯示輸入欄位 + 兩個按鈕的主 Dialog
    AvatarContent(
        memberName = memberName,
        fruit = ui.fruit,
        animal = ui.animal,
        loading = ui.loading,
        error = ui.error,
        onFruitChange = vm::onFruitChange,
        onAnimalChange = vm::onAnimalChange,
        onGenerateClick = { vm.generateImage(memberId) },
        onUsePreviousClick = vm::onUsePreviousClick,
        onDismiss = {
            showDialog = false
            onFinished(memberId, null)
        }
    )
}