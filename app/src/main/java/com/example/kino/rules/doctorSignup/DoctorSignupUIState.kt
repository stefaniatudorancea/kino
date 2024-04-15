package com.example.kino.rules.doctorSignup

data class DoctorSignupUIState(
    var photoUrl: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",

    var firstNameError: Boolean = false,
    var lastNameError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,

    //second screen
    var phoneNumber: String = "",
    var yearsOfExperience: Int = -1,
    var workplace: String = "",
    var university: String = "",
    var graduationYear: Int = 0,
    var linkedln: String = "",
    var privacyPolicyAccepted: Boolean = false,

    var phoneNumberError: Boolean = false,
    var yearsOfExperienceError: Boolean = false,
    var workplaceError: Boolean = false,
    var universityError: Boolean = false,
    var graduationYearError: Boolean = false,
    var linkedlnError: Boolean = false,
    var privacyPolicyError: Boolean = false
)