package com.demo.cleanarchitecturedemo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.cleanarchitecturedemo.domain.model.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val body: String,
    val timestamp: Long // For cache validation
)

fun PostEntity.toDomain(): Post{
    return Post(id, title, body)
}