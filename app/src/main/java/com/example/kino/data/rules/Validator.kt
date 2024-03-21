package com.example.kino.data.rules

object Validator {

    fun validateFirstName(fName: String): ValidationResult{
        return ValidationResult(
            (!fName.isNullOrEmpty() && fName.length >= 3)
        )

    }
    fun validateLastName(lName: String): ValidationResult{
        return ValidationResult(
            (!lName.isNullOrEmpty() && lName.length >= 3)
        )
    }
    fun validateEmail(email: String): ValidationResult{
        return ValidationResult(
            (!email.isNullOrEmpty() && email.length >= 5)
        )
    }
    fun validatePassword(password: String): ValidationResult{
        return ValidationResult(
            (!password.isNullOrEmpty() && password.length>=4)
        )
    }

    fun validatePrivacyPolicyAcceptance(statusValue: Boolean): ValidationResult{
        return ValidationResult(

        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)