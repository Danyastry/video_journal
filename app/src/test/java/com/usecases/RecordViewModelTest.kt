package com.usecases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dvstry.ynd_task.domain.usecases.SaveVideoUseCase
import com.dvstry.ynd_task.presentation.screen.record.RecordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File

@ExperimentalCoroutinesApi
class RecordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var saveVideoUseCase: SaveVideoUseCase
    private lateinit var viewModel: RecordViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        saveVideoUseCase = mock()
        viewModel = RecordViewModel(saveVideoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onRecordingStarted should update state correctly`() {
        assertFalse(viewModel.uiState.value.isRecording)
        viewModel.onRecordingStarted()
        assertTrue(viewModel.uiState.value.isRecording)
        assertNull(viewModel.uiState.value.recordedVideoFile)
        assertFalse(viewModel.uiState.value.saveSuccess)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onVideoSaved should update state with file`() {
        val testFile = mock<File>()
        viewModel.onVideoSaved(testFile)
        assertFalse(viewModel.uiState.value.isRecording)
        assertEquals(testFile, viewModel.uiState.value.recordedVideoFile)
    }

    @Test
    fun `updateDescription should update description state`() {
        assertEquals("", viewModel.uiState.value.description)
        viewModel.updateDescription("Test description")
        assertEquals("Test description", viewModel.uiState.value.description)
    }

    @Test
    fun `saveVideoEntry should call use case and update state on success`() = runTest {
        val mockFile = mock<File>()
        whenever(mockFile.absolutePath).thenReturn("/test/path.mp4")
        whenever(mockFile.exists()).thenReturn(true)
        whenever(saveVideoUseCase(any(), any())).thenReturn(Result.success(123L))

        viewModel.onVideoSaved(mockFile)
        viewModel.updateDescription("Test description")
        viewModel.saveVideoEntry()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(saveVideoUseCase).invoke("/test/path.mp4", "Test description")
        assertTrue(viewModel.uiState.value.saveSuccess)
        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("", viewModel.uiState.value.description)
        assertNull(viewModel.uiState.value.recordedVideoFile)
    }

    @Test
    fun `saveVideoEntry should handle errors`() = runTest {
        val mockFile = mock<File>()
        whenever(mockFile.absolutePath).thenReturn("/test/path.mp4")
        whenever(saveVideoUseCase(any(), any())).thenReturn(
            Result.failure(Exception("Test error"))
        )

        viewModel.onVideoSaved(mockFile)
        viewModel.saveVideoEntry()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("Test error", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.saveSuccess)
    }

    @Test
    fun `discardVideo should delete file and reset state`() {
        val mockFile = mock<File>()
        whenever(mockFile.exists()).thenReturn(true)

        viewModel.onVideoSaved(mockFile)
        viewModel.updateDescription("Test description")
        viewModel.discardVideo()

        verify(mockFile).delete()
        assertNull(viewModel.uiState.value.recordedVideoFile)
        assertEquals("", viewModel.uiState.value.description)
        assertNull(viewModel.uiState.value.error)
    }
}