package com.example.jetpackcompose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY name")
    fun getAllFlow(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE name LIKE '%' || :q || '%' ORDER BY name")
    fun searchFlow(q: String): Flow<List<MemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MemberEntity>)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun delete(id: Long)

    // 只在啟動/初始化用來判斷 DB 是否為空
    @Query("SELECT COUNT(*) FROM members")
    suspend fun countOnce(): Int

    @Query("DELETE FROM members")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(entity: MemberEntity)

    @Query("UPDATE members SET name = :name WHERE id = :id")
    suspend fun updateName(id: Long, name: String)

}
