package com.example.jetpackcompose.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// 要送給 OpenAI 的格式
data class OpenAiImageRequest(
    val model: String ="dall-e-3",
    val prompt: String,
    val size: String = "1024x1024",
    val n: Int = 1
)

// OpenAI 回傳的格式
data class OpenAiImageResponse(
    val created: Long,
    val data: List<OpenAiImageData>
)

data class OpenAiImageData(
    val url: String?
)

interface OpenAiApi {

    @POST("images/generations")
    suspend fun generateImage(
        @Header("Authorization") auth: String,
        @Body body: OpenAiImageRequest
    ): Response<OpenAiImageResponse>
}
