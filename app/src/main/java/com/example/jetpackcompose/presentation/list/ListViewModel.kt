package com.example.jetpackcompose.list

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetpackcompose.data.AvatarRepository
import com.example.jetpackcompose.data.MemberRepository
import com.example.jetpackcompose.data.local.AppDatabase
import com.example.jetpackcompose.data.local.MemberEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListViewModel(
    private val memberRepo: MemberRepository,
    private val avatarRepo: AvatarRepository,
    private val app: Application
) : AndroidViewModel(app) {

    /* -----------------------------------------------------------
     * 1️⃣ 成員清單與搜尋
     * ----------------------------------------------------------- */
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val members: StateFlow<List<MemberEntity>> =
        _query
            .map { it.trim() }
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.isEmpty()) memberRepo.getAllFlow() else memberRepo.searchFlow(q)
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearchChange(q: String) { _query.value = q }
    fun addMember(name: String) = viewModelScope.launch { memberRepo.add(name) }
    fun deleteMember(id: Long) = viewModelScope.launch {
        memberRepo.delete(id)
        avatarRepo.unbindAvatarsByMember(id)
_avatars.update { old -> old - id }
    }
    fun updateMemberName(id: Long, name: String) = viewModelScope.launch { memberRepo.updateName(id, name) }

    fun ensureSeedIfEmpty() = viewModelScope.launch(Dispatchers.IO) { memberRepo.ensureSeedIfEmpty(app) }
    fun reseedFromAssets() = viewModelScope.launch(Dispatchers.IO) { memberRepo.reseedFromAssets(app) }

    /* -----------------------------------------------------------
     * 2️⃣ 頭像快取層（UI 即時顯示用）
     * ----------------------------------------------------------- */
    private val _avatars = MutableStateFlow<Map<Long, Bitmap>>(emptyMap())
    val avatars: StateFlow<Map<Long, Bitmap>> = _avatars.asStateFlow()


    /** 只刷新一個成員的頭像（從 DB 最新一張） */

    fun refreshAvatar(memberId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val bmp = avatarRepo.getLatestAvatarFor(memberId)
            _avatars.update { old ->
                if (bmp != null) {
                    // 有新圖 → 更新
                    old + (memberId to bmp)
                } else {
                    // DB 說這個人現在沒圖了 → 把快取的也拿掉
                    old - memberId
                }
            }
        }
    }

    /* -----------------------------------------------------------
     * 3️⃣ 初始化：載入所有現有頭像
     * ----------------------------------------------------------- */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = avatarRepo.getAllAvatars()
            val map = buildMap<Long, Bitmap> {
                list.forEach { entity ->
                    val ownerId = entity.memberId
                    // 🔴 這裡只塞「有 owner 的」進去，因為 list 畫面需要 key
                    if (ownerId != null && ownerId != 0L) {
                        val bmp = avatarRepo.loadBitmapFromPath(entity.filePath)
                        put(ownerId, bmp)
                    }
                }
            }
            _avatars.value = map
        }
    }
    /* -----------------------------------------------------------
     * 4️⃣ 提供外部查詢用
     * ----------------------------------------------------------- */
    fun getMemberNameById(id: Long): String? =
        members.value.firstOrNull { it.id == id }?.name

    /* -----------------------------------------------------------
     * 5️⃣ Factory
     * ----------------------------------------------------------- */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val db = AppDatabase.getInstance(app)
                val memberRepo = MemberRepository(db.memberDao())
                val avatarRepo = AvatarRepository(
                    api = com.example.jetpackcompose.data.remote.RetrofitProvider.openai(),
                    avatarDao = db.avatarDao(),
                    appContext = app
                )
                ListViewModel(memberRepo, avatarRepo, app)
            }
        }
    }
}