package com.example.kino.rules.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.kino.navigation.Screen

data class NavigationItem(
    val index: Int,
    val titile: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val destination: Screen
)
