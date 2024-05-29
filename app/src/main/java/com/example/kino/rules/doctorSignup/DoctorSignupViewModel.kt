package com.example.kino.rules.doctorSignup

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.data.Validator
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class DoctorSignupViewModel: ViewModel() {
    private val TAG = DoctorSignupViewModel::class.simpleName
    var registartionUIState = mutableStateOf(DoctorSignupUIState())
    var firstValidationsPassed = mutableStateOf(false)
    var secondValidationsPassed = mutableStateOf(false)
    private var signUpInProgress = mutableStateOf(false)
    private var currentUid = FirebaseAuth.getInstance().currentUser?.uid

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> = _imageUrl

    private val _imageName = MutableLiveData<String?>()
    val imageName: LiveData<String?> = _imageName

    fun onEvent(event: DoctorSignupUIEvent){
        when(event){
            is DoctorSignupUIEvent.PhotoChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    photoUrl = event.photoUrl
                )
            }
            is DoctorSignupUIEvent.LastNameChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    lastName = event.lastName
                )
                printState()
            }
            is DoctorSignupUIEvent.FirstNameChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    firstName = event.firstName
                )
                printState()
            }
            is DoctorSignupUIEvent.EmailChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    email = event.email
                )
                printState()
            }
            is DoctorSignupUIEvent.PasswordChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    password = event.password
                )
                printState()
            }
            is DoctorSignupUIEvent.PhoneNumberChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    phoneNumber = event.phoneNumber
                )
                printState()
            }
            is DoctorSignupUIEvent.YearsOfExperienceChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    yearsOfExperience = event.yearsOfExperience
                )
                printState()
            }
            is DoctorSignupUIEvent.WorkplaceChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    workplace = event.workplace
                )
                printState()
            }
            is DoctorSignupUIEvent.UniversityChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    university = event.university
                )
                printState()
            }
            is DoctorSignupUIEvent.GraduationYearChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    graduationYear = event.graduationYear
                )
                printState()
            }
            is DoctorSignupUIEvent.LinkedlnChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    linkedln = event.linkedln
                )
                printState()
            }
            is DoctorSignupUIEvent.PrivacyPolicyCheckBoxClicked -> {
                registartionUIState.value = registartionUIState.value.copy(
                    privacyPolicyAccepted = event.status
                )
                printState()
            }
            is DoctorSignupUIEvent.NextStepButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.DoctorSecondSignupScreen)
            }
            is DoctorSignupUIEvent.RegisterButtonClicked -> {
                signUp()
            }
            is DoctorSignupUIEvent.PatientButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.SignUpScreen)
            }
        }
        validateDataWithRules()
    }
    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, registartionUIState.value.toString())
    }

    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(
            fName = registartionUIState.value.firstName
        )
        val lNameResult = Validator.validateLastName(
            lName = registartionUIState.value.lastName
        )
        val emailResult = Validator.validateEmail(
            email = registartionUIState.value.email
        )
        val phoneNumberResult = Validator.validatePassword(
            password = registartionUIState.value.phoneNumber
        )
        val yearsOfExperienceResult = Validator.validateYearsOfExperience(
            yearsOfExperience = registartionUIState.value.yearsOfExperience
        )
        val workplaceResult = Validator.validateWorkplace(
            workplace = registartionUIState.value.workplace
        )
        val universityResult = Validator.validateUniversity(
            university = registartionUIState.value.university
        )
        val graduationYearResult = Validator.validateGraduationYear(
            graduationYear = registartionUIState.value.graduationYear
        )
        val linkedlnResult = Validator.validatePassword(
            password = registartionUIState.value.linkedln
        )
        val passwordResult = Validator.validatePassword(
            password = registartionUIState.value.password
        )
        val privacyPolicyResult = Validator.validatePrivacyPolicyAcceptance(
            statusValue = !registartionUIState.value.privacyPolicyAccepted
        )
        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult= $fNameResult")
        Log.d(TAG, "lNameResult= $lNameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")
        //Log.d(TAG, "privacyPolicyResult= $privacyPolicyResult")

        registartionUIState.value = registartionUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            privacyPolicyError = privacyPolicyResult.status,
            phoneNumberError = phoneNumberResult.status,
            yearsOfExperienceError = yearsOfExperienceResult.status,
            workplaceError = workplaceResult.status,
            universityError = universityResult.status,
            graduationYearError = graduationYearResult.status,
            linkedlnError = linkedlnResult.status
        )

        if (fNameResult.status && lNameResult.status && emailResult.status && passwordResult.status) {
            firstValidationsPassed.value = true
        } else {
            firstValidationsPassed.value = false
        }

        if(phoneNumberResult.status && yearsOfExperienceResult.status && workplaceResult.status && universityResult.status && graduationYearResult.status){
            secondValidationsPassed.value = true
        } else {
            secondValidationsPassed.value = false
        }
    }
    fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val fileName = "images/${UUID.randomUUID()}.jpg"  // Generăm numele fișierului
        val storageRef = FirebaseStorage.getInstance().reference.child(fileName)
        storageRef.putFile(imageUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl  // Solicităm URL-ul după încărcare
        }.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            _imageName.value = imageUrl  // Salvăm URL-ul complet
        }.addOnFailureListener {
            // Tratați cazurile de eroare, cum ar fi revenirea la o valoare implicită sau gestionarea erorilor
        }
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
        uri?.let { uploadImageToFirebaseStorage(it) }
    }

    private fun signUp() {
        Log.d(TAG, "Inside_signUp")
        printState()
        createUserInFirebase(
            email = registartionUIState.value.email,
            password = registartionUIState.value.password
        )
    }
    private fun createUserInFirebase(email: String, password: String) {
        signUpInProgress.value = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                signUpInProgress.value = false
                if (task.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        addUserDetailsInDatabase(
                            uid = user.uid,
                            firstName = registartionUIState.value.firstName,
                            lastName = registartionUIState.value.lastName,
                            email = registartionUIState.value.email,
                            imageUrl = this.imageName.value,
                            phoneNumber = registartionUIState.value.phoneNumber,
                            yearsOfExperience = registartionUIState.value.yearsOfExperience,
                            workplace = registartionUIState.value.workplace,
                            university = registartionUIState.value.university,
                            graduationYear = registartionUIState.value.graduationYear,
                            linkedln = registartionUIState.value.linkedln
                        )
                    }
                    PostOfficeAppRouter.navigateTo(Screen.PatientsListScreen)
                } else {
                    Log.d(TAG,"User registration failed: ${task.exception}")
                }
            }
            .addOnFailureListener { exception ->
                signUpInProgress.value = false
                Log.d(TAG,"User registration failed: ${exception.localizedMessage}")
            }
    }

    private fun addUserDetailsInDatabase(
        uid: String,
        firstName: String,
        lastName: String,
        email: String,
        imageUrl: String?,
        phoneNumber: String,
        yearsOfExperience: Int,
        workplace: String,
        university: String,
        graduationYear: Int,
        linkedln: String
    ) {
        val doctorData = DoctorData(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            imageUrl = imageUrl,
            phoneNumber = phoneNumber,
            yearsOfExperience = yearsOfExperience,
            workplace = workplace,
            university = university,
            graduationYear = graduationYear,
            linkedln = linkedln
        )

        val db = Firebase.firestore
        db.collection(DOCTOR_NODE).document(uid)
            .set(doctorData)
            .addOnSuccessListener { Log.d(TAG, "User details added to database") }
            .addOnFailureListener { e -> Log.d(TAG, "Error adding user details", e) }
    }

}