package com.example.kino.rules.doctorLogin

sealed class DoctorLoginUIEvent {
    data class EmailChanged(val email:String) : DoctorLoginUIEvent()
    data class PasswordChanged(val password:String) : DoctorLoginUIEvent()
    object LoginButtonClicked: DoctorLoginUIEvent()
    object PatientButtonClicked: DoctorLoginUIEvent()
}