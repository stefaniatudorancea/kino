package com.example.kino.rules.feedbackRoutine

import com.example.kino.rules.chat.ChatUIEvent

sealed class FeedbackRoutineUIEvent {
    object SeeFeedbacksClicked: FeedbackRoutineUIEvent()
}