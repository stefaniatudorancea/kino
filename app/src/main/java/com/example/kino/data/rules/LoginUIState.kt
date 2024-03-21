package com.example.kino.data.rules

import androidx.lifecycle.ViewModel

data class LoginUIState(
    var email: String = "",
    var password: String = "",

    var emailError: Boolean = false,
    var passwordError: Boolean = false,
) {

}