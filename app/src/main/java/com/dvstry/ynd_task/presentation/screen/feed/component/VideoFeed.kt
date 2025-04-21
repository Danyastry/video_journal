package com.dvstry.ynd_task.presentation.screen.feed.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dvstry.ynd_task.domain.model.VideoEntry

@Composable
fun VideoFeed(
    videos: List<VideoEntry>,
    onDelete: (Long) -> Unit,
    onEnterFullscreen: (VideoEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        items(videos) { video ->
            EnhancedVideoItem(
                video = video,
                onDelete = { onDelete(video.id) },
                onEnterFullscreen = { onEnterFullscreen(video) }
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}