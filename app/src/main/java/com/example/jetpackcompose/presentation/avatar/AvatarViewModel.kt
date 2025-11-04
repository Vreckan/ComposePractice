package com.example.jetpackcompose.presentation.avatar

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.AvatarItem
import com.example.jetpackcompose.data.AvatarRepository
import com.example.jetpackcompose.data.local.AppDatabase
import com.example.jetpackcompose.data.remote.RetrofitProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * UI 狀態
 */
data class AvatarUiState(
    val fruit: String = "",
    val animal: String = "",
    val loading: Boolean = false,
    val image: Bitmap? = null,          // 預覽圖
    val savedPath: String? = null,
    val error: String? = null,
    val previousImages: List<AvatarItem> = emptyList(), // 舊圖清單
    val showPreviousPicker: Boolean = false,            // 是否顯示舊圖挑選視窗
    val selectedAvatarId: Long? = null                  // 使用者挑選的舊圖 id
)

/**
 * Avatar ViewModel
 */
class AvatarViewModel(
    private val repo: AvatarRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(AvatarUiState())
    val ui: StateFlow<AvatarUiState> = _ui

    /* ------------------------------------------------------------
     * 1️⃣ 表單變更
     * ------------------------------------------------------------ */
    fun onFruitChange(s: String) = _ui.update { it.copy(fruit = s, error = null) }
    fun onAnimalChange(s: String) = _ui.update { it.copy(animal = s, error = null) }

    /* ------------------------------------------------------------
     * 2️⃣ 生成新圖
     * ------------------------------------------------------------ */
    fun generateImage(memberId: Long) {
        val f = _ui.value.fruit.trim()
        val a = _ui.value.animal.trim()
        if (f.isEmpty() || a.isEmpty()) {
            _ui.update { it.copy(error = "請先輸入水果與動物") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, image = null, error = null, selectedAvatarId = null) }
            try {
                val entity = withTimeout(120_000L) {
                    repo.generateAndSave(memberId = memberId, fruit = f, animal = a)
                }
                val bmp = repo.loadBitmapFromPath(entity.filePath)
                _ui.update {
                    it.copy(
                        loading = false,
                        image = bmp,
                        savedPath = entity.filePath,
                        error = null,
                        selectedAvatarId = null
                    )
                }
            } catch (e: CancellationException) {
                _ui.update { it.copy(loading = false) }
            } catch (e: Exception) {
                _ui.update { it.copy(loading = false, error = e.message ?: "生成失敗") }
            }
        }
    }

    /* ------------------------------------------------------------
     * 3️⃣ 打開舊圖挑選清單
     * ------------------------------------------------------------ */
    fun onUsePreviousClick() {
        viewModelScope.launch {
            _ui.update { it.copy(showPreviousPicker = true, previousImages = emptyList()) }
            try {
                val list = repo.loadRecentAvatars(limit = 50)
                _ui.update { it.copy(previousImages = list) }
            } catch (e: Exception) {
                _ui.update { it.copy(error = "載入舊圖失敗") }
            }
        }
    }

    /* ------------------------------------------------------------
     * 4️⃣ 使用者從舊圖清單挑了一張
     * ------------------------------------------------------------ */
    fun onPreviousPicked(item: AvatarItem) {
        _ui.update {
            it.copy(
                showPreviousPicker = false,
                image = item.bitmap,
                selectedAvatarId = item.id
            )
        }
    }
    /**
     * 這裡多一個 onDone，把「舊主人 id」回傳給畫面
     */
    fun confirmUseImageFromPreview(memberId: Long, onDone: (Long?) -> Unit) {
        val avatarId = _ui.value.selectedAvatarId ?: return

        viewModelScope.launch {
            try {
                // 🔹 呼叫 Repository：可能發生交換，repo 會回傳舊主人的 id（若沒有則為 null）
                val oldOwnerId = repo.rebindAvatarToMember(avatarId, memberId)

                // 🔹 更新 UI 狀態，避免下次誤用
                _ui.update { it.copy(selectedAvatarId = null) }

                // 🔹 通知畫面（AvatarScreen）舊主人的 id
                onDone(oldOwnerId)

            } catch (e: Exception) {
                _ui.update { it.copy(error = "重綁失敗：${e.message}") }
                onDone(null)
            }
        }
    }

    /* ------------------------------------------------------------
     * 6️⃣ 關閉舊圖挑選清單
     * ------------------------------------------------------------ */
    fun dismissPreviousPicker() {
        _ui.update { it.copy(showPreviousPicker = false) }
    }

    /* ------------------------------------------------------------
     * 7️⃣ Factory
     * ------------------------------------------------------------ */
    companion object {
        fun provideFactory(appContext: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val api = RetrofitProvider.openai()
                    val db = AppDatabase.getInstance(appContext)
                    val repo = AvatarRepository(
                        api = api,
                        avatarDao = db.avatarDao(),
                        appContext = appContext
                    )
                    return AvatarViewModel(repo) as T
                }
            }
    }
}