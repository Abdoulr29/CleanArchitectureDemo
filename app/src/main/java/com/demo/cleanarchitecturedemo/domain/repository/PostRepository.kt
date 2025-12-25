package com.demo.cleanarchitecturedemo.domain.repository

import com.demo.cleanarchitecturedemo.domain.model.Post

interface PostRepository {
    suspend fun getPosts(): List<Post>
}