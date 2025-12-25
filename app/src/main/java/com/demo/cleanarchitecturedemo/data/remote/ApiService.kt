package com.demo.cleanarchitecturedemo.data.remote

import com.demo.cleanarchitecturedemo.data.remote.dto.PostDto
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}