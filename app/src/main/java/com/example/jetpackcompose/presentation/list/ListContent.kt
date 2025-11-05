package com.example.jetpackcompose.list

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.R
import com.example.jetpackcompose.data.local.MemberEntity

/* ------------------------- 色票 ------------------------- */
object AppColors {
    val ScreenBg  = Color(0xFFFAF7F2)
    val CardBg    = Color.White
    val EditGray  = Color(0xFF8F8F8F)
    val DeleteRed = Color(0xFFE53935)
}

/* =========================================================
 * 上：主畫面（組裝層）
 * ========================================================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    query: String,
    members: List<MemberEntity>,
    onQueryChange: (String) -> Unit,
    onDeleteConfirm: (Long) -> Unit,
    onImportConfirm: () -> Unit,
    onClearAllConfirm: () -> Unit,
    onAddConfirm: (String) -> Unit,
    onEditConfirm: (Long, String) -> Unit,

    // --- 頭像相關：這裡只負責顯示、發事件，不開 dialog ---
    getAvatar: (Long) -> Bitmap? = { null },      // 依 id 取得目前頭像（沒有就 null）
    onAvatarClick: (Long) -> Unit = {}            // ★ 點頭像要做什麼，交給上層決定
) {
    val currentCount = members.size

    // View 專屬臨時狀態
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showImportConfirm by rememberSaveable { mutableStateOf(false) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var pendingEdit by rememberSaveable { mutableStateOf<Pair<Long, String>?>(null) }
    var showClearAllConfirm by rememberSaveable { mutableStateOf(false) }


    Scaffold(
        containerColor = AppColors.ScreenBg,
        topBar = {
            TopAppBar(
                title = { Text("Members — $currentCount records") },
                actions = {
                    IconButton(onClick = { showClearAllConfirm= true }) {
                        Icon(Icons.Outlined.DeleteSweep, contentDescription = "CLear All")
                    }
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

            SearchField(
                value = query,
                onValueChange = onQueryChange
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = members, key = { it.id }) { m ->
                    ListRow(
                        name = m.name,
                        avatar = getAvatar(m.id),
                        onAvatarClick = { onAvatarClick(m.id) },   // ★ 只往上丟 id
                        onEdit = { pendingEdit = m.id to m.name },
                        onDelete = { pendingDeleteId = m.id }
                    )
                }
            }
        }
    }

    // 刪除
    pendingDeleteId?.let { id ->
        val targetName = members.firstOrNull { it.id == id }?.name
        DeleteConfirmDialog(
            targetName = targetName,
            onConfirm = {
                onDeleteConfirm(id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null }
        )
    }

    // 匯入
    if (showImportConfirm) {
        ImportConfirmDialog(
            onConfirm = { showImportConfirm = false; onImportConfirm() },
            onDismiss = { showImportConfirm = false }
        )
    }
    //清空
    if (showClearAllConfirm) {
        ClearAllConfirmDialog(
            onConfirm = { showClearAllConfirm = false; onClearAllConfirm() },
            onDismiss = { showClearAllConfirm = false }
        )
    }

    // 編輯
    pendingEdit?.let { (id, oldName) ->
        EditMemberDialog(
            initialName = oldName,
            onConfirm = { newName ->
                if (newName.isNotBlank()) onEditConfirm(id, newName.trim())
                pendingEdit = null
            },
            onDismiss = { pendingEdit = null }
        )
    }

    // 新增
    if (showAddDialog) {
        AddMemberDialog(
            onConfirm = { name ->
                if (name.isNotBlank()) onAddConfirm(name.trim())
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

/* =========================================================
 * 中：子元件（原本的都不動）
 * ========================================================= */
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
                painterResource(R.drawable.vector),
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

@Composable
private fun ListRow(
    name: String,
    avatar: Bitmap?,
    onAvatarClick: () -> Unit,
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
            // 頭像
            if (avatar != null) {
                Image(
                    bitmap = avatar.asImageBitmap(),
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onAvatarClick),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onAvatarClick),
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
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onEdit) {
                Icon(
                    painterResource(R.drawable.edit),
                    contentDescription = "Edit",
                    tint = AppColors.EditGray
                )
            }
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

/* --- Dialog: 刪除確認 --- */
@Composable
private fun DeleteConfirmDialog(
    targetName: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("刪除確認") },
        text = {
            Text(
                if (targetName != null)
                    "確定要刪除 $targetName 嗎？此動作無法復原。"
                else
                    "確定要刪除這筆資料嗎？此動作無法復原。"
            )
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("刪除") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

/* --- Dialog: 匯入確認 --- */
@Composable
private fun ImportConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("匯入原資料") },
        text = { Text("此動作會清空目前所有資料，並重新導入原始 JSON。確定繼續？") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("匯入") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
/* --- Dialog: 清空成員 --- */
@Composable
private fun ClearAllConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("清除目前成員") },
        text = { Text("此動作會清空目前成員，生成過的圖片仍可使用") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("清空") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

/* --- Dialog: 新增成員 --- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMemberDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增成員") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = { Text("請輸入姓名") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) { Text("新增") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

/* --- Dialog: 編輯名稱 --- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMemberDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var editName by rememberSaveable(initialName) { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
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
            TextButton(onClick = { onConfirm(editName) }) { Text("儲存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
/* =========================================================
 * 下：Preview 區
 * ========================================================= */
class SampleMembersProvider : PreviewParameterProvider<List<MemberEntity>> {
    override val values = sequenceOf(
        listOf(
            MemberEntity(id = 1, name = "Alice"),
            MemberEntity(id = 2, name = "Bob"),
            MemberEntity(id = 3, name = "Charlie"),
            MemberEntity(id = 4, name = "Diana"),
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
        onClearAllConfirm ={},
        onAddConfirm = {},
        onEditConfirm = { _, _ -> }
    )
}

@Preview(name = "memberListRow - all sample", showBackground = true)
@Composable
fun memberListRowPreview_All(
    @PreviewParameter(SampleMembersProvider::class) members: List<MemberEntity>
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        members.forEach { m ->
            ListRow(
                name = m.name,
                onEdit = {},
                onDelete = {},
                onAvatarClick = {},
                avatar = null
            )
        }
    }
}

@Preview(name = "Dialog - Delete", showBackground = true)
@Composable
fun DeleteDialogPreview(
    @PreviewParameter(SampleMembersProvider::class) members: List<MemberEntity>
) {
    DeleteConfirmDialog(
        targetName = members.first().name,
        onConfirm = {},
        onDismiss = {}
    )
}

@Preview(name = "Dialog - Import", showBackground = true)
@Composable
fun ImportDialogPreview() {
    ImportConfirmDialog(onConfirm = {}, onDismiss = {})
}

@Preview(name = "Dialog - Import", showBackground = true)
@Composable
fun ClearnDialogPreview() {
    ClearAllConfirmDialog(onConfirm = {}, onDismiss = {})
}

@Preview(name = "Dialog - Add", showBackground = true)
@Composable
fun AddDialogPreview() {
    AddMemberDialog(onConfirm = {}, onDismiss = {})
}

@Preview(name = "Dialog - Edit", showBackground = true)
@Composable
fun EditDialogPreview(
    @PreviewParameter(SampleMembersProvider::class) members: List<MemberEntity>
) {
    EditMemberDialog(
        initialName = members[1].name,
        onConfirm = {},
        onDismiss = {}
    )
}