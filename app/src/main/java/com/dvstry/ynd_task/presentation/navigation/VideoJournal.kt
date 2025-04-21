package com.dvstry.ynd_task.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dvstry.ynd_task.presentation.screen.feed.FeedScreen
import com.dvstry.ynd_task.presentation.screen.home.HomeScreen
import com.dvstry.ynd_task.presentation.screen.record.RecordScreen

@Composable
fun VideoJournal() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRecord = {
                    navController.navigate(Screen.Record.route)
                },
                onNavigateToFeed = {
                    navController.navigate(Screen.Feed.route)
                }
            )
        }
        composable(Screen.Record.route) {
            RecordScreen(
                onNavigateToFeed = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        composable(Screen.Feed.route) {
            FeedScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}