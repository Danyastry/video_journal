package com.dvstry.ynd_task.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Record : Screen("record")
    object Feed : Screen("feed")
}