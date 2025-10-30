package com.example.jetpackcompose.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class ListUiState(
    val query: String = "",
    val members: List<Member> = emptyList()
)

class ListViewModel : ViewModel() {
    private val repo = ItemRepository()

    var ui by mutableStateOf(ListUiState())
        private set

    init {
        ui = ui.copy(members = repo.getAll())
    }

    fun onQueryChange(v: String) {
        ui = ui.copy(query = v, members = repo.search(v))
    }

    fun delete(item: Member) {
        repo.delete(item)
        ui = ui.copy(members = repo.search(ui.query))
    }
}
