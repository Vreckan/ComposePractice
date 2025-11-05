package com.example.jetpackcompose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface AvatarDao {

    // ❶ 每次都插一筆新的，不要覆蓋
    @Insert
    suspend fun insert(avatar: AvatarEntity): Long

    // ❷ 把這張圖轉給別的人
    @Query("UPDATE avatars SET memberId = :newMemberId WHERE id = :avatarId")
    suspend fun rebindAvatar(avatarId: Long, newMemberId: Long?)

    // ❸ 用 avatarId 找原本是誰的（給交換用）
    @Query("SELECT * FROM avatars WHERE id = :avatarId LIMIT 1")
    suspend fun getById(avatarId: Long): AvatarEntity?

    // ❹ 撈全部（list 一開始載用）
    @Query("SELECT * FROM avatars")
    suspend fun getAll(): List<AvatarEntity>

    // ❺ 撈這個人「最新一張」頭像
    //    用 createdAt DESC 排一下，確定拿到的是最後那張
    @Query("SELECT * FROM avatars WHERE memberId = :memberId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getByMember(memberId: Long): AvatarEntity?

    // ❻ 刪一張
    @Query("DELETE FROM avatars WHERE id = :id")
    suspend fun deleteById(id: Long)

    // ❼ 刪這個圖的人 「把這個人名下的圖 owner 清空」
    @Query("UPDATE avatars SET memberId = NULL WHERE memberId = :memberId")
    suspend fun unbindByMember(memberId: Long)
    // ❽ 最近幾張（做「Recent images」的那個畫面）
    @Query("SELECT * FROM avatars ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<AvatarEntity>

    // ❾ ✅ 新增：綁定新的頭像時，如果這個人原本有頭像就先拆掉

    @Transaction
    suspend fun bindAvatarToMemberReplacingOld(
        avatarId: Long,
        memberId: Long
    ): Long? {
        // 找這個人現在正在用的那張（可能沒有）
        val oldAvatar = getByMember(memberId)

        // 有舊的 → 先把舊的 owner 清掉
        if (oldAvatar != null) {
            rebindAvatar(oldAvatar.id, null)
        }

        // 再把新的綁給這個人
        rebindAvatar(avatarId, memberId)

        // 回傳舊的那張的 id，讓上層有需要可以處理
        return oldAvatar?.id
    }

    @Query("SELECT COUNT(*) FROM avatars")
    suspend fun countAll(): Int

    @Insert
    suspend fun insertAll(list: List<AvatarEntity>)
}
