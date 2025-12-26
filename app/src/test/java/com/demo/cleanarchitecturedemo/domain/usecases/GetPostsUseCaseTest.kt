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
        // 1. Arrange: Mock the repository to throw an IOException
        coEvery { repository.getPosts() } throws IOException("No Internet")

        // 2. Act: Call the UseCase
        // IMPORTANT: The UseCase must have the try/catch block we added earlier
        val result = useCase.invoke()

        // 3. Assert: Verify it returns failure, NOT throws exception
        assertTrue(result.isFailure)
        assertTrue("Expected DomainError.Network but got ${result.exceptionOrNull()}",
            result.exceptionOrNull() is DomainError.Network)
    }

}