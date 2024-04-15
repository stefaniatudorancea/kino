package com.example.kino.data

data class UserData(
    var uid: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var imageUrl: String? = "",
    var favDoctor: String? = "false",
)