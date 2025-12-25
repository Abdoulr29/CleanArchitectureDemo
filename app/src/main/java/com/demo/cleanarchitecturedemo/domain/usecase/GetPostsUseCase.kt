package com.demo.cleanarchitecturedemo.domain.usecase

import com.demo.cleanarchitecturedemo.domain.model.DomainError
import com.demo.cleanarchitecturedemo.domain.model.Post
import com.demo.cleanarchitecturedemo.domain.repository.PostRepository
import java.io.IOException

class GetPostsUseCase(private val repository: PostRepository) {

    suspend operator fun invoke(): Result<List<Post>> {
        return try {
            Result.success(repository.getPosts())
        } catch (e: IOException) {
            Result.failure(DomainError.Network)
        } catch (e: Exception) {
            Result.failure(DomainError.Unknown)
        }
    }
}