package com.example.jetpackcompose.data

import android.content.Context
import com.example.jetpackcompose.data.local.MemberDao
import com.example.jetpackcompose.data.local.MemberEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

class MemberRepository(
    private val dao: MemberDao
) {
    fun getAllFlow(): Flow<List<MemberEntity>> = dao.getAllFlow()
    fun searchFlow(q: String): Flow<List<MemberEntity>> = dao.searchFlow(q)

    suspend fun add(name: String) =
        dao.insert(MemberEntity(id = 0L, name = name))


    suspend fun delete(id: Long) =
        dao.delete(id)

    suspend fun updateName(id: Long, name: String) =
        dao.updateName(id, name)

    // ✅ 需要 assets 時才傳 Context 進來
    suspend fun ensureSeedIfEmpty(context: Context) {
        if (dao.countOnce() == 0) {
            val json = context.assets.open("members.json")
                .bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<MemberEntity>>() {}.type
            val list: List<MemberEntity> = Gson().fromJson(json, type)
            val normalized = list.map { it.copy(id = 0L) }  // 讓 Room 產主鍵
            dao.insertAll(normalized)
        }
    }

    suspend fun reseedFromAssets(context: Context) {
        dao.deleteAll()
        val json = context.assets.open("members.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<MemberEntity>>() {}.type
        val list: List<MemberEntity> = Gson().fromJson(json, type)
        val normalized = list.map { it.copy(id = 0L) }
        dao.insertAll(normalized)
    }
}
