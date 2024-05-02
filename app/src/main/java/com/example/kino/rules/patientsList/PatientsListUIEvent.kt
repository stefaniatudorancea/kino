package com.example.kino.rules.patientsList

sealed class PatientsListUIEvent {
    object ViewChatButtonClicked: PatientsListUIEvent()
}