package com.demo.cleanarchitecturedemo.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.cleanarchitecturedemo.domain.usecase.GetPostsUseCase
import com.demo.cleanarchitecturedemo.presentation.state.PostsViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostsViewModel(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PostsViewState())
    val state: StateFlow<PostsViewState> = _state

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = getPostsUseCase()

            result.onSuccess { posts ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    posts = posts
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = throwable.message ?: "An unknown error occurred"
                )
            }
        }
    }
}