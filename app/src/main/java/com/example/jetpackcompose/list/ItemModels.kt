package com.example.jetpackcompose.list
import kotlinx.serialization.Serializable

// 定義列表中一筆資料的格式
@Serializable
data class Member(
    val id: Int,
    val name: String
)
