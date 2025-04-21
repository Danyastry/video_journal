package com.dvstry.ynd_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dvstry.ynd_task.presentation.navigation.VideoJournal
import com.dvstry.ynd_task.ui.theme.Ynd_taskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ynd_taskTheme {
                VideoJournal()
            }
        }
    }
}

