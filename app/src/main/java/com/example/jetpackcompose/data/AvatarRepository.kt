package com.example.jetpackcompose.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.jetpackcompose.BuildConfig
import com.example.jetpackcompose.data.local.AvatarDao
import com.example.jetpackcompose.data.local.AvatarEntity
import com.example.jetpackcompose.data.remote.OpenAiApi
import com.example.jetpackcompose.data.remote.OpenAiImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

data class AvatarItem(
    val id: Long,
    val bitmap: Bitmap
)

class AvatarRepository(
    private val api: OpenAiApi,
    private val avatarDao: AvatarDao,
    private val appContext: Context
) {

    private val http = OkHttpClient()

    /* ------------------------------------------------------------
     * 1) 生成一張新的頭像，插入一筆（不覆蓋舊的）
     * ------------------------------------------------------------ */
    suspend fun generateAndSave(memberId: Long, fruit: String, animal: String): AvatarEntity {
        val prompt = buildString {
            append("a single cute mascot character, ")
            append("hybrid of $fruit and $animal, ")
            append("close-up full body view, large in frame, centered composition, ")
            append("plain pastel background, high contrast, ")
            append("no text, no border, no shadow, ")
            append("digital illustration, colorful cute sticker style, high quality, vibrant colors")
        }

        val bitmap = generateBitmap(prompt)

        // 檔名記得帶時間戳，避免覆蓋本地檔
        val file = saveBitmapToLocal(
            bitmap,
            "avatar_${memberId}_${System.currentTimeMillis()}"
        )

        val entity = AvatarEntity(
            memberId = memberId,
            filePath = file.absolutePath
        )
        val newId = avatarDao.insert(entity)
        return entity.copy(id = newId)
    }

    /* ------------------------------------------------------------
     * 2) OpenAI & 檔案工具
     * ------------------------------------------------------------ */
    private suspend fun generateBitmap(prompt: String): Bitmap {
        val token = BuildConfig.OPENAI_API_KEY

        val res = api.generateImage(
            auth = "Bearer $token",
            body = OpenAiImageRequest(
                prompt = prompt,
                // 你的 data class 預設 size = "1024x1024"
                n = 1
            )
        )

        if (!res.isSuccessful) {
            error("OpenAI API ${res.code()} ${res.errorBody()?.string()}")
        }

        val url = res.body()?.data?.firstOrNull()?.url
            ?: error("No image url")
        return downloadImage(url)
    }

    private suspend fun downloadImage(url: String): Bitmap = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(url).build()
        http.newCall(req).execute().use { resp ->
            val bytes = resp.body?.bytes() ?: error("Empty body")
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    private suspend fun saveBitmapToLocal(bitmap: Bitmap, fileName: String): File =
        withContext(Dispatchers.IO) {
            val dir = appContext.filesDir
            val file = File(dir, "$fileName.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }
            file
        }

    /* ------------------------------------------------------------
     * 3) 對外方法
     * ------------------------------------------------------------ */

    // 從路徑載圖
    suspend fun loadBitmapFromPath(path: String): Bitmap = withContext(Dispatchers.IO) {
        val file = File(path)
        val bytes = file.readBytes()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    // 撈「這個人最新一張頭像」給 list 用
    suspend fun getLatestAvatarFor(memberId: Long): Bitmap? = withContext(Dispatchers.IO) {
        val entity = avatarDao.getByMember(memberId) ?: return@withContext null
        val file = File(entity.filePath)
        if (!file.exists()) return@withContext null
        val bytes = file.readBytes()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    // 一開始載全部
    suspend fun getAllAvatars(): List<AvatarEntity> = withContext(Dispatchers.IO) {
        avatarDao.getAll()
    }

    // 🔹 刪除某個人的所有頭像
    // 刪成員時用：圖保留，但 owner 變 null
    suspend fun unbindAvatarsByMember(memberId: Long) = withContext(Dispatchers.IO) {
        avatarDao.unbindByMember(memberId)
    }

    // 最近幾張 → 給「Use previous」那層
    suspend fun loadRecentAvatars(limit: Int = 50): List<AvatarItem> =
        withContext(Dispatchers.IO) {
            avatarDao.getRecent(limit).mapNotNull { entity ->
                val f = File(entity.filePath)
                if (f.exists()) {
                    val bmp = BitmapFactory.decodeFile(f.absolutePath)
                    AvatarItem(id = entity.id, bitmap = bmp)
                } else null
            }
        }

    // 選舊圖 → 給新的人 → 如果新的人原本有圖，就跟舊主人交換
    suspend fun rebindAvatarToMember(
        avatarId: Long,
        memberId: Long
    ): Long? = withContext(Dispatchers.IO) {

        // 1) 被使用者在「recent images」點到的那張圖
        val picked = avatarDao.getById(avatarId) ?: return@withContext null
        val oldOwnerId = picked.memberId   // 可能是 null

        // 2) 這次要接收頭像的人，現在有沒有自己的圖
        val targetCurrent = avatarDao.getByMember(memberId)  // 可能是 null

        // 3) 先把這兩張都解綁，避免中間一瞬間一個人兩張
        avatarDao.rebindAvatar(picked.id, null)
        if (targetCurrent != null) {
            avatarDao.rebindAvatar(targetCurrent.id, null)
        }

        // 4) 把「被點的那張」給這個人
        avatarDao.rebindAvatar(picked.id, memberId)

        // 5) 如果原本那張圖有主人，而且主人不是自己，再把「原本的圖」還回去
        if (oldOwnerId != null && oldOwnerId != memberId && targetCurrent != null) {
            avatarDao.rebindAvatar(targetCurrent.id, oldOwnerId)
        }

        // 6) 把舊主人回傳出去給 Nav 刷
        oldOwnerId
    }

}