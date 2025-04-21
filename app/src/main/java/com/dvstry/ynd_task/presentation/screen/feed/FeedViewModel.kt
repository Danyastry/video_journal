package com.dvstry.ynd_task.presentation.screen.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.ynd_task.domain.usecases.DeleteVideoUseCase
import com.dvstry.ynd_task.domain.usecases.GetVideosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FeedViewModel(
    private val getVideosUseCase: GetVideosUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) : ViewModel() {

    data class FeedUiState(
        val videos: List<VideoEntry> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentlyPlayingId: Long? = null
    )

    private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
    val uiState: StateFlow<FeedUiState> = _uiState

    init {
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getVideosUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
                .collect { videos ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        videos = videos
                    )
                }
        }
    }

    fun setCurrentlyPlaying(videoId: Long?) {
        _uiState.value = _uiState.value.copy(currentlyPlayingId = videoId)
    }

    fun deleteVideo(videoId: Long) {
        viewModelScope.launch {
            deleteVideoUseCase(videoId)
                .onSuccess {
                    // - Video deleted successfully, reload the list
                    // - The flow will automatically update the UI
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to delete video"
                    )
                }
        }
    }
}