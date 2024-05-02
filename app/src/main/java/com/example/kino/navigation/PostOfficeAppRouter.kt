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
    object DoctorProfileScreen: Screen()
    object DoctorFirstSignupScreen: Screen()
    object DoctorSecondSignupScreen: Screen()
    object DoctorLoginScreen: Screen()
    object ProfileDoctorScreen: Screen()
    object PatientsListScreen: Screen()
    object RoutinesScreen: Screen()
    object ExercisesScreen: Screen()
    object DoctorChatScreen: Screen()
    object AssignRoutinesScreen: Screen()
    object CreateExerciseScreen: Screen()
    object CreateRoutineScreen: Screen()
}

object PostOfficeAppRouter{
    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.SignUpScreen)
    private val screenStack: MutableList<Screen> = mutableListOf(Screen.SignUpScreen)

    fun navigateTo(destination: Screen){
        screenStack.add(destination)
        currentScreen.value = destination
    }

    fun goBack() {
        if (screenStack.size > 1) { // Asigură-te că există unde să te întorci
            screenStack.removeAt(screenStack.size - 1) // Scoate ultimul element
            currentScreen.value = screenStack.last() // Setează penultimul element ca ecran curent
        }
    }
}