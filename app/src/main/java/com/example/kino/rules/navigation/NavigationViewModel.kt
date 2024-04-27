package com.example.kino.rules.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.app.EventBus
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import kotlinx.coroutines.launch

class NavigationViewModel: ViewModel() {
    val navigationItemsList = listOf<NavigationItem>(
        NavigationItem(
            index = 0,
            titile = "Doctors",
            selectedIcon = Icons.Filled.People,
            unselectedIcon = Icons.Outlined.People,
            hasNews = false,
            badgeCount = null,
            destination = Screen.DoctorsScreen
        ),
        NavigationItem(
            index = 1,
            titile = "Chats",
            selectedIcon = Icons.AutoMirrored.Filled.Chat,
            unselectedIcon = Icons.AutoMirrored.Outlined.Chat,
            hasNews = false,
            badgeCount = null,
            destination = Screen.ChatsScreen
        ),
        NavigationItem(
            index = 2,
            titile = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false,
            badgeCount = null,
            destination = Screen.ProfileScreen
        ),
    )

    fun onEvent(event: NavigationUIEvent) {
        when (event) {
            is NavigationUIEvent.HomeButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.DoctorsScreen)
            }
            is NavigationUIEvent.ChatsButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.ChatsScreen)
            }
            is NavigationUIEvent.RoutineButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.RoutineScreen)
            }
            is NavigationUIEvent.ProfileButtonClicked -> {
                viewModelScope.launch {
                    EventBus.postEvent("TriggerAction")
                }
                PostOfficeAppRouter.navigateTo(Screen.ProfileScreen)
            }

        }
    }


}