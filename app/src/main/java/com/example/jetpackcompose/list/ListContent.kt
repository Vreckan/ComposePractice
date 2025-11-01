package com.example.jetpackcompose.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.R
import com.example.jetpackcompose.data.local.MemberEntity

object AppColors {
    val ScreenBg  = Color(0xFFFAF7F2)   // 米白底
    val CardBg    = Color.White
    val EditGray  = Color(0xFF8F8F8F)
    val DeleteRed = Color(0xFFE53935)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    query: String,
    members: List<MemberEntity>,
    onQueryChange: (String) -> Unit,
    onDeleteConfirm: (Long) -> Unit,
    onImportConfirm: () -> Unit,
    onAddConfirm: (String) -> Unit,
    onEditConfirm: (Long, String) -> Unit,
) {
    val currentCount = members.size

    // —— 只屬於 View 的臨時狀態 ——
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showImportConfirm by rememberSaveable { mutableStateOf(false) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var newName by rememberSaveable { mutableStateOf("") }
    var pendingEdit by rememberSaveable { mutableStateOf<Pair<Long, String>?>(null) }

    Scaffold(
        containerColor = AppColors.ScreenBg,
        topBar = {
            TopAppBar(
                title = { Text("Members — $currentCount records") },
                actions = {
                    IconButton(onClick = { showImportConfirm = true }) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = "Import default data")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Add")
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // 搜尋框（Figma 風格）
            SearchField(
                value = query,
                onValueChange = onQueryChange
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp), // 給 FAB 空間
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = members, key = { it.id }) { m ->
                    ListRow(
                        name = m.name,
                        onEdit = { pendingEdit = m.id to m.name },
                        onDelete = { pendingDeleteId = m.id }
                    )
                }
            }
        }
    }

    // —— 刪除確認 ——
    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("刪除確認") },
            text = { Text("確定要刪除這筆資料嗎？此動作無法復原。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteConfirm(pendingDeleteId!!)
                        pendingDeleteId = null
                    }
                ) { Text("刪除") }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteId = null }) { Text("取消") } }
        )
    }

    // —— 匯入確認 ——
    if (showImportConfirm) {
        AlertDialog(
            onDismissRequest = { showImportConfirm = false },
            title = { Text("匯入原資料") },
            text = { Text("此動作會清空目前所有資料，並重新導入原始 JSON。確定繼續？") },
            confirmButton = {
                TextButton(onClick = { showImportConfirm = false; onImportConfirm() }) { Text("匯入") }
            },
            dismissButton = { TextButton(onClick = { showImportConfirm = false }) { Text("取消") } }
        )
    }

    // —— 編輯對話框 ——
    pendingEdit?.let { (id, oldName) ->
        var editName by rememberSaveable(id) { mutableStateOf(oldName) }
        AlertDialog(
            onDismissRequest = { pendingEdit = null },
            title = { Text("重新命名") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    singleLine = true,
                    placeholder = { Text("輸入新名稱") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editName.isNotBlank()) onEditConfirm(id, editName.trim())
                    pendingEdit = null
                }) { Text("儲存") }
            },
            dismissButton = { TextButton(onClick = { pendingEdit = null }) { Text("取消") } }
        )
    }

    // —— 新增對話框 ——
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { newName = ""; showAddDialog = false },
            title = { Text("新增成員") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    placeholder = { Text("請輸入姓名") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) onAddConfirm(newName.trim())
                    newName = ""; showAddDialog = false
                }) { Text("新增") }
            },
            dismissButton = {
                TextButton(onClick = { newName = ""; showAddDialog = false }) { Text("取消") }
            }
        )
    }
}

/* ------------------------- 子元件：搜尋框（Figma 風格） ------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = { Text("Search") },
        singleLine = true,
        leadingIcon = {
            Icon(
                painterResource(R.drawable.vector), // 若你有專用 search 圖，換成 R.drawable.ic_search
                contentDescription = null,
                tint = AppColors.EditGray
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppColors.CardBg,
            unfocusedContainerColor = AppColors.CardBg,
            focusedBorderColor = Color(0xFFE3E3E3),
            unfocusedBorderColor = Color(0xFFEAEAEA),
            cursorColor = AppColors.EditGray
        )
    )
}

/* ------------------------- 子元件：每一列（卡片 + 頭像 + 兩個 icon） ------------------------- */

@Composable
private fun ListRow(
    name: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.CardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側圓形頭像（用你自己的使用者 icon）
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Color(0xFFF0F0F0)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painterResource(R.drawable.vector),
                        contentDescription = null,
                        tint = Color(0xFF6B6B6B),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // 編輯（灰）
            IconButton(onClick = onEdit) {
                Icon(
                    painterResource(R.drawable.edit),
                    contentDescription = "Edit",
                    tint = AppColors.EditGray
                )
            }
            // 刪除（紅）
            IconButton(onClick = onDelete) {
                Icon(
                    painterResource(R.drawable.trash),
                    contentDescription = "Delete",
                    tint = AppColors.DeleteRed
                )
            }
        }
    }
}

/* ------------------------- Preview ------------------------- */

class SampleMembersProvider : PreviewParameterProvider<List<MemberEntity>> {
    override val values = sequenceOf(
        listOf(
            MemberEntity(id = 1, name = "Alice"),
            MemberEntity(id = 2, name = "Bob"),
            MemberEntity(id = 3, name = "Charlie"),
            MemberEntity(id = 4, name = "Diana")
        )
    )
}

@Preview(name = "ListContent - sample", showBackground = true)
@Composable
fun ListContentPreview_Sample(
    @PreviewParameter(SampleMembersProvider::class) members: List<MemberEntity>
) {
    ListContent(
        query = "",
        members = members,
        onQueryChange = {},
        onDeleteConfirm = {},
        onImportConfirm = {},
        onAddConfirm = {},
        onEditConfirm = { _, _ -> }
    )
}
