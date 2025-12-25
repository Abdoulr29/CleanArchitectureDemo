package com.demo.cleanarchitecturedemo.domain.usecase

import com.demo.cleanarchitecturedemo.domain.model.Post
import com.demo.cleanarchitecturedemo.domain.repository.PostRepository

class GetPostsUseCase(private val repository: PostRepository) {

    suspend operator fun invoke(): Result<List<Post>> {
        return try {
            Result.success(repository.getPosts())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}