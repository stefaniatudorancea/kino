package com.example.kino.rules.doctorLogin

data class DoctorLoginUIState(
    var email: String = "",
    var password: String = "",

    var emailError: Boolean = false,
    var passwordError: Boolean = false,
) {

}