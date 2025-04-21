package com.usecases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.ynd_task.domain.usecases.DeleteVideoUseCase
import com.dvstry.ynd_task.domain.usecases.GetVideosUseCase
import com.dvstry.ynd_task.presentation.screen.feed.FeedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

@ExperimentalCoroutinesApi
class FeedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getVideosUseCase: GetVideosUseCase
    private lateinit var deleteVideoUseCase: DeleteVideoUseCase
    private lateinit var viewModel: FeedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getVideosUseCase = mock()
        deleteVideoUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadVideos should update state with videos from use case`() = runTest {
        val mockVideos = listOf(
            VideoEntry(id = 1, filePath = "path1", createdAt = Date()),
            VideoEntry(id = 2, filePath = "path2", createdAt = Date())
        )
        whenever(getVideosUseCase()).thenReturn(flowOf(mockVideos))

        viewModel = FeedViewModel(getVideosUseCase, deleteVideoUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockVideos, viewModel.uiState.value.videos)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `setCurrentlyPlaying should update currentlyPlayingId`() = runTest {
        whenever(getVideosUseCase()).thenReturn(flowOf(emptyList()))
        viewModel = FeedViewModel(getVideosUseCase, deleteVideoUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setCurrentlyPlaying(42L)

        assertEquals(42L, viewModel.uiState.value.currentlyPlayingId)
    }

    @Test
    fun `deleteVideo should call deleteVideoUseCase with correct id`() = runTest {
        whenever(getVideosUseCase()).thenReturn(flowOf(emptyList()))
        whenever(deleteVideoUseCase(123L)).thenReturn(Result.success(true))
        viewModel = FeedViewModel(getVideosUseCase, deleteVideoUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteVideo(123L)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(deleteVideoUseCase).invoke(123L)
    }

    @Test
    fun `deleteVideo should update error state when use case returns failure`() = runTest {
        whenever(getVideosUseCase()).thenReturn(flowOf(emptyList()))
        whenever(deleteVideoUseCase(123L)).thenReturn(Result.failure(Exception("Test error")))
        viewModel = FeedViewModel(getVideosUseCase, deleteVideoUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteVideo(123L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Test error", viewModel.uiState.value.error)
    }
}