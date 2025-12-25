package com.demo.cleanarchitecturedemo.data.mappers

import com.demo.cleanarchitecturedemo.data.local.PostEntity
import com.demo.cleanarchitecturedemo.data.local.toDomain
import com.demo.cleanarchitecturedemo.data.remote.dto.PostDto
import com.demo.cleanarchitecturedemo.data.remote.dto.toEntity
import junit.framework.Assert.assertEquals
import org.junit.Test

class PostMapperTest {

    @Test
    fun `PostDto to PostEntity maps correctly and adds timestamp`() {
        val dto = PostDto(id = 1, userId = 1, title = "Hello", body = "World")
        val timestamp = 1000L

        val entity = dto.toEntity(timestamp)

        assertEquals(1, entity.id)
        assertEquals("Hello", entity.title)
        assertEquals("World", entity.body)
        assertEquals(1000L, entity.timestamp)
    }

    @Test
    fun `PostEntity to Domain Post maps correctly`() {
        val entity = PostEntity(id = 1, title = "Hello", body = "World", timestamp = 1000L)

        val domain = entity.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Hello", domain.title)
        assertEquals("World", domain.body)
    }
}