package com.demo.cleanarchitecturedemo.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: PostDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.postDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun insertAndReadAllPosts() = runBlocking {
        val posts = listOf(PostEntity(1, "Title", "Body", 0L))
        dao.insertPosts(posts)
        val result = dao.getAllPosts()
        assertEquals(1, result.size)
        assertEquals("Title", result[0].title)
    }
}