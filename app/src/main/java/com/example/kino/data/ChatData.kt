package com.example.kino.data

data class ChatData(
    val chatId: String = "",
    val patient: String = "",
    val doctor: String = "",
    val lastMessage: Message?
)

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: String = "",
)

data class ChatListItem(
    val firstName: String="",
    val lastName: String="",
    val time: String="",
    val text: Message?,
    val imageUrl: String=""
)
