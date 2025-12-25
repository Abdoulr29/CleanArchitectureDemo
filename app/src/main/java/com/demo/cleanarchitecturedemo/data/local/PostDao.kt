package com.demo.cleanarchitecturedemo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>): List<Long>

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostEntity>

    @Query("DELETE FROM posts")
    suspend fun clearPosts()
}