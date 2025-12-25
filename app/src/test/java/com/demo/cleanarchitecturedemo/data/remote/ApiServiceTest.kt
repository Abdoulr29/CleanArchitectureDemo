package com.demo.cleanarchitecturedemo.data.remote

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.get
import okhttp3.mockwebserver.MockResponse

class ApiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var api: ApiService

    @Before
    fun setup() {
        server = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun teardown() = server.shutdown()

    @Test
    fun `getPosts parses JSON response correctly`() = runTest {
        // Mock a successful JSON response
        val json = """[{"id": 1, "userId": 1, "title": "Test Title", "body": "Test Body"}]"""
        server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        val result = api.getPosts()

        assertEquals(1, result.size)
        assertEquals("Test Title", result[0].title)
    }
}