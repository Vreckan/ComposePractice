package com.example.jetpackcompose.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetpackcompose.data.MemberRepository
import com.example.jetpackcompose.data.local.AppDatabase
import com.example.jetpackcompose.data.local.MemberEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListViewModel(
    private val repo: MemberRepository,
    app: Application
) : AndroidViewModel(app) {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val members: StateFlow<List<MemberEntity>> =
        _query
            .map { it.trim() }
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.isEmpty())
                    repo.getAllFlow() else repo.searchFlow(q)
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearchChange(q: String) { _query.value = q }

    fun addMember(name: String) = viewModelScope.launch { repo.add(name) }

    fun deleteMember(id: Long) = viewModelScope.launch { repo.delete(id) }

    fun updateMemberName(id: Long, name: String) =
        viewModelScope.launch { repo.updateName(id, name) }

    // 只有需要 assets 時才用 Application Context（乾淨、安全）
    fun ensureSeedIfEmpty() = viewModelScope.launch(Dispatchers.IO) {
        repo.ensureSeedIfEmpty(getApplication())
    }

    fun reseedFromAssets() = viewModelScope.launch(Dispatchers.IO) {
        repo.reseedFromAssets(getApplication())
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as Application
                val dao = AppDatabase.get(app).memberDao()
                val repo = MemberRepository(dao)
                ListViewModel(repo, app)
            }
        }
    }
}
