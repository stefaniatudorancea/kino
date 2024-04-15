package com.example.kino.rules.chat

sealed class ChatUIEvent {
    data class ChatFieldChanged(val chatField:String) : ChatUIEvent()
    object SendButtonClicked: ChatUIEvent()
}