package com.usecases

import com.dvstry.ynd_task.domain.repository.VideoRepository
import com.dvstry.ynd_task.domain.usecases.SaveVideoUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SaveVideoUseCaseTest {

    private lateinit var repository: VideoRepository
    private lateinit var saveVideoUseCase: SaveVideoUseCase

    @Before
    fun setUp() {
        repository = mock()
        saveVideoUseCase = SaveVideoUseCase(repository)
    }

    @Test
    fun `invoke should generate thumbnail and save video to repository`() = runTest {
        val testFilePath = "/test/video.mp4"
        val testDescription = "Test description"
        val testThumbnailPath = "/test/thumbnail.jpg"
        whenever(repository.generateThumbnail(testFilePath)).thenReturn(testThumbnailPath)
        whenever(repository.saveVideo(any())).thenReturn(123L)
        val result = saveVideoUseCase(testFilePath, testDescription)

        verify(repository).generateThumbnail(testFilePath)
        verify(repository).saveVideo(any())
        assertTrue(result.isSuccess)
        assertEquals(123L, result.getOrNull())
    }

    @Test
    fun `invoke should handle missing thumbnail gracefully`() = runTest {
        val testFilePath = "/test/video.mp4"
        val testDescription = "Test description"
        whenever(repository.generateThumbnail(testFilePath)).thenReturn(null)
        whenever(repository.saveVideo(any())).thenReturn(123L)
        val result = saveVideoUseCase(testFilePath, testDescription)

        verify(repository).generateThumbnail(testFilePath)
        verify(repository).saveVideo(any())
        assertTrue(result.isSuccess)
        assertEquals(123L, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        val testFilePath = "/test/video.mp4"
        val testDescription = "Test description"
        val testException = RuntimeException("Test error")
        whenever(repository.generateThumbnail(testFilePath)).thenReturn("/test/thumbnail.jpg")
        whenever(repository.saveVideo(any())).thenThrow(testException)
        val result = saveVideoUseCase(testFilePath, testDescription)

        assertTrue(result.isFailure)
        assertEquals(testException, result.exceptionOrNull())
    }
}