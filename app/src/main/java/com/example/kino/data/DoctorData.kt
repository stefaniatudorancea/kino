package com.example.kino.data

data class DoctorData(
    var uid: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var imageUrl: String? = "",
    var phoneNumber: String = "",
    var yearsOfExperience: Int = 0,
    var workplace: String = "",
    var university: String = "",
    var graduationYear: Int = 0,
    var linkedln: String = "",
    var privacyPolicyAccepted: Boolean = false,
)

data class DoctorReview(
    var firstName: String = "",
    var lastName: String = "",
    var textReview: String = "",
    var timestamp: String = "",
)
