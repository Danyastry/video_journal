package com.usecases

import com.dvstry.ynd_task.domain.repository.VideoRepository
import com.dvstry.ynd_task.domain.usecases.DeleteVideoUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeleteVideoUseCaseTest {

    private lateinit var repository: VideoRepository
    private lateinit var deleteVideoUseCase: DeleteVideoUseCase

    @Before
    fun setUp() {
        repository = mock()
        deleteVideoUseCase = DeleteVideoUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository deletes video successfully`() = runTest {
        val videoId = 123L
        whenever(repository.deleteVideo(videoId)).thenReturn(true)
        val result = deleteVideoUseCase(videoId)

        verify(repository).deleteVideo(videoId)
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails to delete video`() = runTest {
        val videoId = 123L
        whenever(repository.deleteVideo(videoId)).thenReturn(false)
        val result = deleteVideoUseCase(videoId)

        verify(repository).deleteVideo(videoId)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Failed to delete video") == true)
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        val videoId = 123L
        val testException = RuntimeException("Test error")
        whenever(repository.deleteVideo(videoId)).thenThrow(testException)
        val result = deleteVideoUseCase(videoId)

        verify(repository).deleteVideo(videoId)
        assertTrue(result.isFailure)
        assertEquals(testException, result.exceptionOrNull())
    }
}