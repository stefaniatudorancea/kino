package com.example.kino.rules.navigation
sealed class NavigationUIEvent(){
    object HomeButtonClicked: NavigationUIEvent()
    object ChatsButtonClicked: NavigationUIEvent()
    object RoutineButtonClicked: NavigationUIEvent()

    object ProfileButtonClicked: NavigationUIEvent()
}