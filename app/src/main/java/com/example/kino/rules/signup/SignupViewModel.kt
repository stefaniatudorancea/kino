package com.example.kino.rules.signup

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserData
import com.example.kino.data.Validator
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class SignupViewModel : ViewModel() {

    private val TAG = SignupViewModel::class.simpleName
    var registartionUIState = mutableStateOf(SignUpUIState())
    var allValidationsPassed = mutableStateOf(false)
    var signUpInProgress = mutableStateOf(false)

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> = _imageUrl
    fun onEvent(event: SignupUIEvent) {

        when (event) {
            is SignupUIEvent.PhotoChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    photoUrl = event.photoUrl
                )
                printState()
            }
            is SignupUIEvent.FirstNameChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    firstName = event.firstName
                )
                printState()
            }
            is SignupUIEvent.LastNameChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    lastName = event.lastName
                )
                printState()
            }

            is SignupUIEvent.EmailChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    email = event.email
                )
                printState()
            }

            is SignupUIEvent.PasswordChanged -> {
                registartionUIState.value = registartionUIState.value.copy(
                    password = event.password
                )
                printState()
            }
            is SignupUIEvent.PrivacyPolicyCheckBoxClicked -> {
                registartionUIState.value = registartionUIState.value.copy(
                    privacyPolicyAccepted = event.status
                )
                printState()
            }
            is SignupUIEvent.RegisterButtonClicked -> {
                signUp()
            }
            is SignupUIEvent.PhysioteraphistButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.DoctorFirstSignupScreen)
            }
        }
        validateDataWithRules()
    }

    private fun signUp() {
        Log.d(TAG, "Inside_signUp")
        printState()

        createUserInFirebase(
            email = registartionUIState.value.email,
            password = registartionUIState.value.password
        )


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
        Log.d(TAG, "privacyPolicyResult= $privacyPolicyResult")
        Log.d(TAG, "photoUrlResult= $privacyPolicyResult")


        registartionUIState.value = registartionUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            privacyPolicyError = privacyPolicyResult.status
        )

        if (fNameResult.status && lNameResult.status && emailResult.status && passwordResult.status) {
            allValidationsPassed.value = true
        } else {
            allValidationsPassed.value = false
        }
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, registartionUIState.value.toString())
    }

    private fun createUserInFirebase(email: String, password: String) {
        signUpInProgress.value = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d(TAG,"Insider_OnCompleteListener")
                Log.d(TAG,"isSuccessful = ${it.isSuccessful}, ${email}, ${password}")
                signUpInProgress.value = false
                if(it.isSuccessful){
                    addUserDetailsInDatabase(
                        firstName = registartionUIState.value.firstName,
                        lastName = registartionUIState.value.lastName,
                        email = registartionUIState.value.email,
                        imageUrl = this.imageUrl.value
                    )
                    PostOfficeAppRouter.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_OnFailureListener")
                Log.d(TAG,"Exception = ${it.message}")
                Log.d(TAG,"Exception = ${it.localizedMessage}")
            }

    }

    private fun addUserDetailsInDatabase(firstName: String, lastName: String, email: String, imageUrl: String?) {
        var currentUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUid != null) {
            val userData = UserData(uid = currentUid, firstName = firstName, lastName = lastName, email = email, imageUrl = imageUrl, favDoctor = "")
            val db = FirebaseFirestore.getInstance()

            db.collection(USER_NODE).document(userData.uid)
                .set(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "User successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                }
        } else {
            Log.w(TAG, "Failed to get user ID")
        }
        }

    fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}.jpg")
        storageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                _imageUrl.value = uri.toString()
            }
        }.addOnFailureListener {
            //exception -> {onEvent(exception)}
        }
    }
    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
        uri?.let { uploadImageToFirebaseStorage(it) }
    }
}