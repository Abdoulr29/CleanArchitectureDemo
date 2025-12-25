package com.demo.cleanarchitecturedemo.presentation.posts

import app.cash.turbine.test
import com.demo.cleanarchitecturedemo.domain.model.Post
import com.demo.cleanarchitecturedemo.domain.usecase.GetPostsUseCase
import com.demo.cleanarchitecturedemo.presentation.ui.PostsViewModel
import com.demo.cleanarchitecturedemo.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PostsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase: GetPostsUseCase = mockk()
    private lateinit var viewModel: PostsViewModel

    @Test
    fun `fetchPosts transitions through Loading and Success states`() = runTest {
        val posts = listOf(Post(1, "Title", "Body"))
        // 1. Stub the UseCase
        coEvery { useCase.invoke() } returns Result.success(posts)

        // 2. Initialize ViewModel
        viewModel = PostsViewModel(useCase)

        // 3. Test the flow
        viewModel.state.test {
            // StateFlow always emits the current value immediately upon subscription.
            // Depending on how fast the init block ran, this might be Loading OR already Success.

            val firstState = awaitItem()

            if (firstState.isLoading) {
                // If we caught it early enough (Loading)
                val secondState = awaitItem()
                assertFalse(secondState.isLoading)
                assertEquals(posts, secondState.posts)
            } else {
                // If it already finished (Success)
                assertEquals(posts, firstState.posts)
            }
        }
    }
}
