package com.example.jetpackcompose.list

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun ListScreen(vm: ListViewModel = viewModel(factory = ListViewModel.Factory)) {
    val query by vm.query.collectAsState()
    val members by vm.members.collectAsState(initial = emptyList())

    // 對話框狀態都交給 ListContent 管理，不在這裡寫
    ListContent(
        query = query,
        members = members,
        onQueryChange = vm::onSearchChange,
        onDeleteConfirm = vm::deleteMember,
        onImportConfirm = vm::reseedFromAssets,
        onAddConfirm = vm::addMember,
        onEditConfirm = vm::updateMemberName
    )
}
