package com.example.jetpackcompose.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "avatars",
    indices = [Index(value = ["memberId"], unique = true)] // 🔹 保證一個人只有一張頭像
)
data class AvatarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val memberId: Long?,          // 綁 members 表的 id
    val filePath: String,        // 圖片實際存在手機的路徑
    val createdAt: Long = System.currentTimeMillis()
)
