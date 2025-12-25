package com.demo.cleanarchitecturedemo.presentation.state

import com.demo.cleanarchitecturedemo.domain.model.Post

data class PostsViewState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)