package com.usecases

import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.ynd_task.domain.repository.VideoRepository
import com.dvstry.ynd_task.domain.usecases.GetVideosUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class GetVideosUseCaseTest {

    private lateinit var repository: VideoRepository
    private lateinit var getVideosUseCase: GetVideosUseCase

    @Before
    fun setUp() {
        repository = mock()
        getVideosUseCase = GetVideosUseCase(repository)
    }

    @Test
    fun `invoke should return videos from repository`() = runTest {
        val mockVideos = listOf(
            VideoEntry(id = 1, filePath = "path1", createdAt = Date()),
            VideoEntry(id = 2, filePath = "path2", createdAt = Date())
        )
        whenever(repository.getVideos()).thenReturn(flowOf(mockVideos))
        val result = getVideosUseCase().first()

        verify(repository).getVideos()
        assertEquals(mockVideos, result)
    }

    @Test
    fun `invoke should return empty list when repository returns empty`() = runTest {
        whenever(repository.getVideos()).thenReturn(flowOf(emptyList()))
        val result = getVideosUseCase().first()

        verify(repository).getVideos()
        assertEquals(emptyList<VideoEntry>(), result)
    }
}