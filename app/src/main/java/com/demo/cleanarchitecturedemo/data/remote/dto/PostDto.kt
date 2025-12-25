package com.demo.cleanarchitecturedemo.data.remote.dto

import com.demo.cleanarchitecturedemo.data.local.PostEntity
import com.demo.cleanarchitecturedemo.domain.model.Post

data class PostDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

fun PostDto.toEntity(): PostEntity{
    return PostEntity(id, title, body, System.currentTimeMillis())
}