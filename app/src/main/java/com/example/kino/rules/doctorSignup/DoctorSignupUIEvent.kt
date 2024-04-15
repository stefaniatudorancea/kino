package com.example.kino.rules.doctorSignup

sealed class DoctorSignupUIEvent {
    data class PhotoChanged(val photoUrl: String): DoctorSignupUIEvent()
    data class FirstNameChanged(val firstName:String) : DoctorSignupUIEvent()
    data class LastNameChanged(val lastName:String) : DoctorSignupUIEvent()
    data class EmailChanged(val email:String) : DoctorSignupUIEvent()
    data class PasswordChanged(val password:String) : DoctorSignupUIEvent()
    data class PhoneNumberChanged(val phoneNumber:String) : DoctorSignupUIEvent()
    data class YearsOfExperienceChanged(val yearsOfExperience: Int) : DoctorSignupUIEvent()
    data class WorkplaceChanged(val workplace:String) : DoctorSignupUIEvent()
    data class UniversityChanged(val university:String) : DoctorSignupUIEvent()
    data class GraduationYearChanged(val graduationYear: Int) : DoctorSignupUIEvent()
    data class LinkedlnChanged(val linkedln:String) : DoctorSignupUIEvent()
    data class PrivacyPolicyCheckBoxClicked(val status: Boolean) : DoctorSignupUIEvent()
    object NextStepButtonClicked: DoctorSignupUIEvent()
    object RegisterButtonClicked: DoctorSignupUIEvent()
    object PatientButtonClicked: DoctorSignupUIEvent()
}