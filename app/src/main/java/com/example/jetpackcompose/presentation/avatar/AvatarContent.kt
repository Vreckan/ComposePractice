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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
 * ① 主畫面：這個就是 Dialog + 輸入 + 按鈕
 * ───────────────────────────────────────────── */
@Composable
fun AvatarContent(
    memberName: String,
    fruit: String,
    animal: String,
    loading: Boolean,
    error: String?,
    onFruitChange: (String) -> Unit,
    onAnimalChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onUsePreviousClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Generate new avatar for $memberName") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
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
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Generate")
                        }
                    }

                    OutlinedButton(
                        onClick = onUsePreviousClick,
                        enabled = !loading,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        // 改短避免被擠
                        Text("Use recent")
                    }
                }

                Spacer(Modifier.height(12.dp))

                when {
                    loading -> Text("Generating…")
                    error != null -> Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !loading) {
                Text("Close")
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = !loading
        )
    )
}

/* ─────────────────────────────────────────────
 * ② 生成後的「Use this image?」Dialog
 * ───────────────────────────────────────────── */
@Composable
fun GeneratedPreviewDialog(
    image: Bitmap,
    onUse: () -> Unit,
    onRegenerate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onRegenerate,
        title = { Text("Use this image?") },
        text = {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )
        },
        confirmButton = {
            TextButton(onClick = onUse) { Text("Use this image") }
        },
        dismissButton = {
            TextButton(onClick = onRegenerate) { Text("Generate again") }
        }
    )
}

/* ─────────────────────────────────────────────
 * ③ 最近圖片全螢幕
 * ───────────────────────────────────────────── */
@Composable
fun PreviousImagesFullscreen(
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

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun Preview_AvatarContent() {
    MaterialTheme {
        AvatarContent(
            memberName = "Cindy",
            fruit = "Apple",
            animal = "Fox",
            loading = false,
            error = null,
            onFruitChange = {},
            onAnimalChange = {},
            onGenerateClick = {},
            onUsePreviousClick = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun Preview_GeneratedPreviewDialog() {
    val bmp = remember {
        Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.rgb(255, 150, 180))
        }
    }
    MaterialTheme {
        GeneratedPreviewDialog(
            image = bmp,
            onUse = {},
            onRegenerate = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun Preview_AvatarPreviousFullscreen() {
    val items = List(6) { idx ->
        val bmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.rgb(220, 180 - idx * 15, 130 + idx * 10))
        }
        AvatarItem(id = idx.toLong(), bitmap = bmp)
    }

    MaterialTheme {
        PreviousImagesFullscreen(
            images = items,
            onSelect = {},
            onClose = {}
        )
    }
}