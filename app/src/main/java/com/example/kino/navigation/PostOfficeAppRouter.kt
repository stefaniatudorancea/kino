package com.example.kino.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen(){
    object SignUpScreen: Screen()
    object LoginScreen: Screen()
    object TermsAndConditionsScreen: Screen()
    object HomeScreen: Screen()
    object ChatsScreen: Screen()
    object RoutineScreen: Screen()
    object DoctorsScreen: Screen()
    object ProfileScreen: Screen()
    object DoctorFirstSignupScreen: Screen()
    object DoctorSecondSignupScreen: Screen()
    object DoctorLoginScreen: Screen()
    object DoctorProfileScreen: Screen()
}

object PostOfficeAppRouter{
    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.DoctorsScreen)

    fun navigateTo(destination: Screen){
        currentScreen.value = destination
    }
}