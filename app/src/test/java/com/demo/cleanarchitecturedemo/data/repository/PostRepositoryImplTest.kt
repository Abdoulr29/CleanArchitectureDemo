package com.demo.cleanarchitecturedemo.data.repository

import com.demo.cleanarchitecturedemo.data.local.PostDao
import com.demo.cleanarchitecturedemo.data.local.PostEntity
import com.demo.cleanarchitecturedemo.data.remote.ApiService
import com.demo.cleanarchitecturedemo.data.remote.dto.PostDto
import com.demo.cleanarchitecturedemo.domain.model.Post
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest
import java.io.IOException

class PostRepositoryImplTest : KoinTest {

    private val api: ApiService = mockk()
    private val dao: PostDao = mockk(relaxed = true)
    private lateinit var repository: PostRepositoryImpl

    @Before
    fun setup() {
        repository = PostRepositoryImpl(api, dao)
    }

    @Test
    fun `when cache is fresh, repository returns cache and never calls API`() = runTest {
        val freshTimestamp = System.currentTimeMillis() - 1000L // 1 sec ago
        val cached = listOf(PostEntity(1, "Cached", "Body", freshTimestamp))
        coEvery { dao.getAllPosts() } returns cached

        val result = repository.getPosts()

        assertEquals("Cached", result.first().title)
        coVerify(exactly = 0) { api.getPosts() }
    }

    //    @Test
//    fun `when cache is stale, repository calls API and updates database`() = runTest {
//        val staleTimestamp = System.currentTimeMillis() - (10 * 60 * 1000L) // 10 mins ago
//        val staleData = listOf(PostEntity(1, "Old", "Old", staleTimestamp))
//        val networkData = listOf(PostDto(1, 1, "New", "New"))
//
//        coEvery { dao.getAllPosts() } returns staleData
//        coEvery { api.getPosts() } returns networkData
//
//        val result = repository.getPosts()
//
//        assertEquals("New", result.first().title)
//        coVerify(exactly = 1) { dao.clearPosts() }
//        coVerify(exactly = 1) { dao.insertPosts(any()) }
//    }

//
//    @Test
//    fun `when cache is stale, repository calls API and updates database`() = runTest {
//        // 1. Arrange
//        val apiPosts = listOf(PostDto(1, 1, "API Title", "API Body"))
//        val domainPosts = listOf(Post(1, "API Title", "API Body"))
//
//        // Create the entity that the DAO should return AFTER the insert
//        val updatedEntity = PostEntity(1, "API Title", "API Body", System.currentTimeMillis())
//
//        // Mock DAO:
//        // 1. First call (check cache): Returns empty/stale list -> triggers API call
//        // 2. Second call (fetch result): Returns the new updated entity
//        coEvery { dao.getAllPosts() } returnsMany listOf(emptyList(), listOf(updatedEntity))
//
//        coEvery { api.getPosts() } returns apiPosts
//        coEvery { dao.insertPosts(any()) } returns listOf(1L)
//
//        // 2. Act
//        // Since getPosts() is a suspend function returning List<Post>, we just call it.
//        val result = repository.getPosts()
//
//        // 3. Assert
//        // result is List<Post>. domainPosts is List<Post>. This compares them correctly.
//        assertEquals(domainPosts, result)
//
//        // Verify API was called
//        coVerify(exactly = 1) { api.getPosts() }
//
//        // Verify DB update happened
//        coVerify(exactly = 1) { dao.insertPosts(any()) }
//    }


    @Test
    fun `when network fails and cache exists, repository returns stale cache`() = runTest {
        coEvery { dao.getAllPosts() } returns listOf(PostEntity(1, "Stale", "Body", 1L))
        coEvery { api.getPosts() } throws IOException()

        val result = repository.getPosts()

        assertEquals("Stale", result.first().title)
    }
}