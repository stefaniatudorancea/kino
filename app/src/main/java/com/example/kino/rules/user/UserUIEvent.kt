package com.example.kino.rules.user

sealed class UserUIEvent {
    object LogoutButtonClicked: UserUIEvent()
}