package com.dvstry.ynd_task.presentation.screen.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvstry.ynd_task.domain.usecases.SaveVideoUseCase
import com.dvstry.ynd_task.presentation.screen.record.state.RecordUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class RecordViewModel(
    private val saveVideoUseCase: SaveVideoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    fun onRecordingStarted() {
        _uiState.value = _uiState.value.copy(
            isRecording = true,
            recordedVideoFile = null,
            saveSuccess = false,
            error = null
        )
    }

    fun onVideoSaved(file: File) {
        _uiState.value = _uiState.value.copy(
            isRecording = false,
            recordedVideoFile = file
        )
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun saveVideoEntry() {
        val state = _uiState.value
        val file = state.recordedVideoFile ?: return

        _uiState.value = state.copy(isSaving = true, error = null)

        viewModelScope.launch {
            saveVideoUseCase(
                filePath = file.absolutePath,
                description = state.description
            )
                .onSuccess { _ ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveSuccess = true,
                        description = "",
                        recordedVideoFile = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Failed to save video"
                    )
                }
        }
    }

    fun discardVideo() {
        _uiState.value.recordedVideoFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        _uiState.value = _uiState.value.copy(
            recordedVideoFile = null,
            description = "",
            error = null
        )
    }
}