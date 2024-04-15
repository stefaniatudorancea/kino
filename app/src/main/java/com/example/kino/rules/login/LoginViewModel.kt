package com.example.kino.rules.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.kino.data.Validator
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {
    private val TAG = LoginViewModel::class.simpleName
    var loginUIState = mutableStateOf(LoginUIState())
    var allValidationsPassed = mutableStateOf(false)
    var loginInProcess = mutableStateOf(false)

    fun onEvent(event: LoginUIEvent){
        when(event){
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email
                )
            }
            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
            }
            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
            is LoginUIEvent.PhysioteraphistButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.DoctorLoginScreen)
            }
            is LoginUIEvent.PhysioteraphistButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.DoctorLoginScreen)
            }
        }
        validateLoginUIDataWithRules()
    }

    private fun login(){
        loginInProcess.value = true
        val email = loginUIState.value.email
        val password = loginUIState.value.password
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                loginInProcess.value = false
                Log.d(TAG, "Inside_login_success")
                Log.d(TAG, "${it.isSuccessful}, ${email}, ${password}")

                if(it.isSuccessful){
                    loginInProcess.value = false
                    PostOfficeAppRouter.navigateTo(Screen.ChatsScreen)
                }
            }
            .addOnFailureListener{
                Log.d(TAG,"Inside_login_failure")
                Log.d(TAG, "${it.localizedMessage}")
            }
    }

    private fun validateLoginUIDataWithRules(){
        val emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )
        val passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )
        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )

        allValidationsPassed.value = emailResult.status && passwordResult.status
    }
}