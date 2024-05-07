package com.example.kino.rules.doctorProfile

import com.example.kino.rules.chat.ChatUIEvent

sealed class DoctorProfileUIEvent {
    data class ReviewChanged(val review:String): DoctorProfileUIEvent()
    object AddReviewButtonClicked: DoctorProfileUIEvent()
    object SeeReviewsTextClicked: DoctorProfileUIEvent()
    object ConfirmDialogButtonClicked: DoctorProfileUIEvent()
}