package com.example.jetpackcompose.list

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// 模擬資料來源：假 JSON 字串（非真 API）
class ItemRepository {
    private var members = mutableListOf<Member>()

    init {
        val jsonSeed = """
            [
              {"id":1,"name":"Terry"},
              {"id":2,"name":"James"},
              {"id":3,"name":"Peter"},
              {"id":4,"name":"Sam"},
              {"id":5,"name":"Jerry"},
              {"id":6,"name":"Thomas"},
              {"id":7,"name":"May"}
            ]
        """.trimIndent()
        members = Json.decodeFromString(jsonSeed)
    }

    fun getAll(): List<Member> = members
    fun search(query: String): List<Member> =
        if (query.isBlank()) members
        else members.filter { it.name.contains(query, ignoreCase = true) }

    fun delete(target: Member) {
        members.removeIf { it.id == target.id }
    }

    fun update(target: Member) {
        val index = members.indexOfFirst { it.id == target.id }
        if (index >= 0) members[index] = target
    }
}
