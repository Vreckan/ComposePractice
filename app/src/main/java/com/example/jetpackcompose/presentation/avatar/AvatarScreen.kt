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

    // 1) 最近圖片
    if (ui.showPreviousPicker) {
        PreviousImagesFullscreen(
            images = ui.previousImages,
            onSelect = { item -> vm.onPreviousPicked(item) },
            onClose = vm::dismissPreviousPicker
        )
        return
    }

    // 2) 有預覽圖 → 顯示 Use this image
    if (ui.image != null) {
        GeneratedPreviewDialog(
            image = ui.image!!,
            onUse = {
                vm.applyCurrentPreview(memberId) { oldOwnerId ->
                    showDialog = false
                    onFinished(memberId, oldOwnerId)
                }
            },
            onRegenerate = {
                vm.clearPreview()
            }
        )
        return
    }

    // 3) 預設主畫面
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