package com.example.kino.rules.doctorProfile

import com.example.kino.rules.chat.ChatUIEvent

sealed class DoctorProfileUIEvent {
    object ConfirmDialogButtonClicked: DoctorProfileUIEvent()
}