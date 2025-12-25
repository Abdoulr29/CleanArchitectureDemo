package com.demo.cleanarchitecturedemo.data.repository

import com.demo.cleanarchitecturedemo.data.local.PostDao
import com.demo.cleanarchitecturedemo.data.local.toDomain
import com.demo.cleanarchitecturedemo.data.remote.ApiService
import com.demo.cleanarchitecturedemo.data.remote.dto.toEntity
import com.demo.cleanarchitecturedemo.domain.model.Post
import com.demo.cleanarchitecturedemo.domain.repository.PostRepository

class PostRepositoryImpl(
    private val api: ApiService, private val dao: PostDao
) : PostRepository {

    override suspend fun getPosts(): List<Post> {
        val cachedPosts = dao.getAllPosts()
        if (cachedPosts.isNotEmpty()) {
            return cachedPosts.map { it.toDomain() }
        }
        val networkPosts = api.getPosts()
        val entities = networkPosts.map { it.toEntity() }
        dao.insertPosts(entities)
        return entities.map { it.toDomain() }
    }
}