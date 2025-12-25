package com.demo.cleanarchitecturedemo.domain.usecases

import com.demo.cleanarchitecturedemo.domain.model.DomainError
import com.demo.cleanarchitecturedemo.domain.repository.PostRepository
import com.demo.cleanarchitecturedemo.domain.usecase.GetPostsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class GetPostsUseCaseTest {
    private val repository: PostRepository = mockk()
    private val useCase = GetPostsUseCase(repository)

    @Test
    fun `when repository throws exception, usecase returns failure with DomainError`() = runTest {
        coEvery { repository.getPosts() } throws IOException("No Internet")

        val result = useCase.invoke()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DomainError.Network)
    }
}