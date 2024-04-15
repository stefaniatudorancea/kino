package com.example.kino.data

object Validator {

    fun validateFirstName(fName: String): ValidationResult {
        return ValidationResult(
            (fName.isNotEmpty() && fName.length >= 3)
        )

    }
    fun validateLastName(lName: String): ValidationResult {
        return ValidationResult(
            (lName.isNotEmpty() && lName.length >= 3)
        )
    }
    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (email.isNotEmpty() && email.length >= 5)
        )
    }
    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            (password.isNotEmpty() && password.length>=4)
        )
    }

    fun validatePhoneNumber(phoneNumber: String): ValidationResult {
        return ValidationResult(
            (phoneNumber.isNotEmpty() && phoneNumber.length>=10)
        )
    }
    fun validateYearsOfExperience(yearsOfExperience: Int): ValidationResult {
        return ValidationResult(
            (yearsOfExperience>=0)
        )
    }

    fun validateWorkplace(workplace: String): ValidationResult {
        return ValidationResult(
            (workplace.isNotEmpty() && workplace.length>=4)
        )
    }
    fun validateUniversity(university: String): ValidationResult {
        return ValidationResult(
            (university.isNotEmpty() && university.length>=4)
        )
    }
    fun validateGraduationYear(graduationYear: Int): ValidationResult {
        return ValidationResult(
            (graduationYear>=1920)
        )
    }
    fun validateLinkedln(linkedln: String): ValidationResult {
        return ValidationResult(
            linkedln.isEmpty()
        )
    }
    fun validatePrivacyPolicyAcceptance(statusValue: Boolean): ValidationResult {
        return ValidationResult(
            statusValue
        )
    }

    fun validateChatField(chatField: String): ValidationResult{
        return ValidationResult(
            chatField.isNotEmpty()
        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)