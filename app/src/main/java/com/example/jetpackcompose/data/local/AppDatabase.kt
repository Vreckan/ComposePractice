package com.example.jetpackcompose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MemberEntity::class, AvatarEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class  AppDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun avatarDao(): AvatarDao   // ★ 新增
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                )
                    .fallbackToDestructiveMigration()   // ← 版本或 schema 變更時自動重建
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
