package com.example.kino.app

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    private val _loadChatIdEvent = MutableSharedFlow<String>()
    val loadChatIdEvent: SharedFlow<String> = _loadChatIdEvent.asSharedFlow()

    suspend fun postEvent(event: String) {
        _events.emit(event)
    }
}