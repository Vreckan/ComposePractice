package com.example.jetpackcompose.presentation.avatar

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.jetpackcompose.data.AvatarItem

/* ─────────────────────────────────────────────
 * 輸入區塊
 * ───────────────────────────────────────────── */
@Composable
fun AvatarContent(
    fruit: String,
    animal: String,
    loading: Boolean,
    error: String?,
    onFruitChange: (String) -> Unit,
    onAnimalChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onUsePreviousClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = fruit,
                onValueChange = onFruitChange,
                label = { Text("Fruit") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = animal,
                onValueChange = onAnimalChange,
                label = { Text("Animal") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGenerateClick,
                    enabled = !loading,
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    if (loading)
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    else Text("Generate")
                }

                OutlinedButton(
                    onClick = onUsePreviousClick,
                    enabled = !loading,
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Use previous")
                }
            }

            Spacer(Modifier.height(12.dp))

            when {
                loading -> Text("Generating…")
                error != null -> Text(error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/* ─────────────────────────────────────────────
 * Dialog 本體（全部事件都丟出去）
 * ───────────────────────────────────────────── */
@Composable
fun AvatarDialog(
    memberName: String,
    // 狀態
    fruit: String,
    animal: String,
    loading: Boolean,
    error: String?,
    image: Bitmap?,
    showPreviousPicker: Boolean,
    previousImages: List<AvatarItem>,
    // 事件
    onFruitChange: (String) -> Unit,
    onAnimalChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onUsePreviousClick: () -> Unit,
    onConfirmGenerated: (Bitmap) -> Unit,
    onSelectPrevious: (AvatarItem) -> Unit,
    onClosePrevious: () -> Unit,
    onDismiss: () -> Unit,
) {
    var showPreview by remember { mutableStateOf(false) }

    // 有新圖就開第二層預覽
    LaunchedEffect(image) {
        if (image != null) showPreview = true
    }

    // ❶ 如果「正在選舊圖」，就只畫舊圖頁，不畫第一層 Dialog
    if (showPreviousPicker) {
        PreviousImagesFullscreen(
            images = previousImages,
            onSelect = onSelectPrevious,
            onClose = onClosePrevious
        )
        return   // ← 直接結束，不要往下畫 AlertDialog
    }

    // ❷ 第一層：輸入 Dialog
    AlertDialog(
        onDismissRequest = {
            if (!loading && !showPreview) onDismiss()
        },
        title = { Text("Generate new avatar for $memberName") },
        text = {
            AvatarContent(
                fruit = fruit,
                animal = animal,
                loading = loading,
                error = error,
                onFruitChange = onFruitChange,
                onAnimalChange = onAnimalChange,
                onGenerateClick = onGenerateClick,
                onUsePreviousClick = onUsePreviousClick
            )
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !loading) {
                Text("Close")
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = !loading && !showPreview
        )
    )

    // ❸ 第二層：生成後預覽
    if (showPreview) {
        image?.let { bmp ->
            AlertDialog(
                onDismissRequest = { showPreview = false },
                title = { Text("Use this image?") },
                text = {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Generated avatar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentScale = ContentScale.Crop
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onConfirmGenerated(bmp)
                        showPreview = false
                        //onDismiss()
                    }) { Text("Use this image") }
                },
                dismissButton = {
                    TextButton(onClick = { showPreview = false }) {
                        Text("Generate again")
                    }
                },
                properties = DialogProperties(dismissOnClickOutside = true)
            )
        }
    }
}
/* ─────────────────────────────────────────────
 * 滿版舊圖選單
 * ───────────────────────────────────────────── */
@Composable
private fun PreviousImagesFullscreen(
    images: List<AvatarItem>,
    onSelect: (AvatarItem) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent images", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onClose) { Text("Close") }
                }

                if (images.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No previous images")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(120.dp),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(images) { item ->
                            Image(
                                bitmap = item.bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelect(item) },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
 * Previews
 * ───────────────────────────────────────────── */
@Preview(showBackground = true)
@Composable
private fun Preview_AvatarInput() {
    MaterialTheme {
        AvatarContent(
            fruit = "Apple",
            animal = "Fox",
            loading = false,
            error = null,
            onFruitChange = {},
            onAnimalChange = {},
            onGenerateClick = {},
            onUsePreviousClick = {}
        )
    }
}


@Preview
@Composable
private fun Preview_AvatarGeneratedDialog() {
    val fake = remember {
        Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.LTGRAY)
        }
    }

    MaterialTheme {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Use this image?") },
            text = {
                Image(
                    bitmap = fake.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentScale = ContentScale.Crop
                )
            },
            confirmButton = {
                TextButton(onClick = {}) { Text("Use this image") }
            },
            dismissButton = {
                TextButton(onClick = {}) { Text("Generate again") }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun Preview_AvatarPreviousFullscreen() {
    val items = remember {
        List(6) { idx ->
            val bmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
            bmp.eraseColor(android.graphics.Color.rgb(210, 140 + idx * 8, 120))
            AvatarItem(
                id = idx.toLong(),
                bitmap = bmp
            )
        }
    }
    PreviousImagesFullscreen(
        images = items,
        onSelect = {},
        onClose = {}
    )
}

