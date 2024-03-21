package com.example.kino.data.rules

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.auth

class SignupViewModel : ViewModel() {

    private val TAG = SignupViewModel::class.simpleName
    var registartionUIState = mutableStateOf(RegistrationUIState())
    var allValidationsPassed = mutableStateOf(false)
    var signUpInProgress = mutableStateOf(false)
    fun onEvent(event: SignupUIEvent) {
        validateDataWithRules()
        when (event) {
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
        }
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
            statusValue = registartionUIState.value.privacyPolicyAccepted
        )

        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult= $fNameResult")
        Log.d(TAG, "lNameResult= $lNameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")
        Log.d(TAG, "privacyPolicyResult= $privacyPolicyResult")


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
                    PostOfficeAppRouter.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_OnFailureListener")
                Log.d(TAG,"Exception = ${it.message}")
                Log.d(TAG,"Exception = ${it.localizedMessage}")
            }
    }

    fun logout(){
        val fireBaseAuth = FirebaseAuth.getInstance()
        var auth: FirebaseAuth
        auth = Firebase.auth
        val currentUser = auth.currentUser
        fireBaseAuth.signOut()
        Log.d(TAG, "current: $currentUser")
        val authStateListener = AuthStateListener{
            if(it.currentUser == null){
                Log.d(TAG, "Inside sign out success")
                PostOfficeAppRouter.navigateTo(Screen.SignUpScreen)
            }else{
                Log.d(TAG, "Inside sign out is not complete")
            }
        }
        fireBaseAuth.addAuthStateListener(authStateListener)
    }

}