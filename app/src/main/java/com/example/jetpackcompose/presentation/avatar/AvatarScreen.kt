package com.example.jetpackcompose.presentation.avatar

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AvatarScreen(
    memberId: Long,
    memberName: String,
    // 回去要知道：這次設定的那個人 +（可能）被換走的那個人
    onFinished: (updatedMemberId: Long, oldOwnerMemberId: Long?) -> Unit
) {
    val ctx = LocalContext.current
    val vm: AvatarViewModel = viewModel(factory = AvatarViewModel.provideFactory(ctx))
    val ui by vm.ui.collectAsState()

    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AvatarDialog(
            memberName = memberName,

            // 狀態
            fruit = ui.fruit,
            animal = ui.animal,
            loading = ui.loading,
            error = ui.error,
            image = ui.image,
            showPreviousPicker = ui.showPreviousPicker,
            previousImages = ui.previousImages,

            // 表單事件
            onFruitChange = vm::onFruitChange,
            onAnimalChange = vm::onAnimalChange,
            onGenerateClick = { vm.generateImage(memberId) },
            onUsePreviousClick = vm::onUsePreviousClick,

            // ✅ 按「Use this image」
            onConfirmGenerated = {
                val pickedOldImageId = ui.selectedAvatarId
                if (pickedOldImageId != null) {
                    // 這次是「從舊圖挑的」→ 要去做 rebind，可能會有舊主人
                    vm.confirmUseImageFromPreview(memberId) { oldOwnerId ->
                        showDialog = false
                        onFinished(memberId, oldOwnerId)
                    }
                } else {
                    // 這次是「剛剛生成的新圖」→ 不用 rebind，直接回 list
                    showDialog = false
                    onFinished(memberId, null)
                }
            },

            // 最近圖片挑一張 → 只放到預覽
            onSelectPrevious = { item ->
                vm.onPreviousPicked(item)
            },

            // 關掉最近圖片清單
            onClosePrevious = vm::dismissPreviousPicker,

            // 按 Close
            onDismiss = {
                showDialog = false
                onFinished(memberId, null)
            }
        )
    }
}