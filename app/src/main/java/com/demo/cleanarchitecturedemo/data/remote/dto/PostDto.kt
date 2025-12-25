package com.demo.cleanarchitecturedemo.data.remote.dto

import com.demo.cleanarchitecturedemo.data.local.PostEntity
import com.demo.cleanarchitecturedemo.domain.model.Post
import java.sql.Timestamp

data class PostDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

fun PostDto.toEntity(timestamp: Long): PostEntity{
    return PostEntity(id, title, body, timestamp)
}