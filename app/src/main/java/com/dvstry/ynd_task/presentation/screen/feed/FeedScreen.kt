package com.dvstry.ynd_task.presentation.screen.feed

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.ynd_task.presentation.screen.feed.component.EmptyFeedScreen
import com.dvstry.ynd_task.presentation.screen.feed.component.FullscreenVideoDialog
import com.dvstry.ynd_task.presentation.screen.feed.component.LoadingScreen
import com.dvstry.ynd_task.presentation.screen.feed.component.VideoFeed
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var fullscreenVideoEntry by remember { mutableStateOf<VideoEntry?>(null) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    if (fullscreenVideoEntry != null) {
        LaunchedEffect(fullscreenVideoEntry) {
            viewModel.setCurrentlyPlaying(null)
        }

        FullscreenVideoDialog(
            videoEntry = fullscreenVideoEntry!!,
            onDismiss = { fullscreenVideoEntry = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Videos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }

                uiState.videos.isEmpty() -> {
                    EmptyFeedScreen()
                }

                else -> {
                    VideoFeed(
                        videos = uiState.videos,
                        onDelete = { videoId ->
                            viewModel.deleteVideo(videoId)
                        },
                        onEnterFullscreen = { videoEntry ->
                            fullscreenVideoEntry = videoEntry
                        }
                    )
                }
            }
        }
    }
}
