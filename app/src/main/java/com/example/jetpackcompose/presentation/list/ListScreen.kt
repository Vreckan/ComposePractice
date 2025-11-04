package com.example.jetpackcompose.list

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ListScreen(
    navController: NavController,
    onAvatarClick: (Long, String) -> Unit,
    listVm: ListViewModel = viewModel(factory = ListViewModel.Factory)
) {
    // 基本資料
    val query by listVm.query.collectAsState()
    val members by listVm.members.collectAsState(initial = emptyList())
    val avatars by listVm.avatars.collectAsState()

    // 這一頁自己的 backStackEntry
    val currentEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = currentEntry?.savedStateHandle

    // 監聽「這次換到頭像的人」
    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            ?.getStateFlow<Long?>("avatar_changed_member_id", null)
            ?.collect { id ->
                if (id != null && id > 0) {
                    listVm.refreshAvatar(id)
                    savedStateHandle["avatar_changed_member_id"] = null
                }
            }
    }

    // 監聽「被換走的人」
    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            ?.getStateFlow<Long?>("avatar_swapped_member_id", null)
            ?.collect { id ->
                if (id != null && id > 0) {
                    listVm.refreshAvatar(id)
                    savedStateHandle["avatar_swapped_member_id"] = null
                }
            }
    }

    // 畫面
    ListContent(
        query = query,
        members = members,
        onQueryChange = listVm::onSearchChange,
        onDeleteConfirm = listVm::deleteMember,
        onImportConfirm = listVm::reseedFromAssets,
        onAddConfirm = listVm::addMember,
        onEditConfirm = listVm::updateMemberName,
        getAvatar = { id -> avatars[id] },
        onAvatarClick = { id ->
            val name = members.firstOrNull { it.id == id }?.name ?: "Member"
            onAvatarClick(id, name)
        }
    )
}