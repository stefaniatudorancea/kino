package com.example.kino.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.screens.patientScreens.ChatsScreen
import com.example.kino.screens.doctorScreens.DoctorLoginScreen
import com.example.kino.screens.doctorScreens.DoctorFirstSignupScreen
import com.example.kino.screens.patientScreens.DoctorProfileScreen
import com.example.kino.screens.doctorScreens.DoctorSecondSignupScreen
import com.example.kino.screens.patientScreens.DoctorsScreen
import com.example.kino.screens.patientScreens.HomeScreen
import com.example.kino.screens.patientScreens.LoginScreen
import com.example.kino.screens.patientScreens.ProfileScreen
import com.example.kino.screens.patientScreens.RoutineScreen
import com.example.kino.screens.patientScreens.SignUpScreen
import com.example.kino.screens.patientScreens.TermnAndConditionsSceen

@Composable
fun PostOfficeApp(){
    Surface(modifier = Modifier.fillMaxSize(),
        color = Color.White){
        Crossfade(targetState = PostOfficeAppRouter.currentScreen, label = ""){ currentState->
            when(currentState.value){
                is Screen.SignUpScreen -> {
                    SignUpScreen()
                }
                is Screen.TermsAndConditionsScreen -> {
                    TermnAndConditionsSceen()
                }
                is Screen.LoginScreen -> {
                    LoginScreen()
                }
                is Screen.HomeScreen -> {
                    HomeScreen()
                }
                is Screen.ChatsScreen -> {
                    ChatsScreen()
                }
                is Screen.RoutineScreen -> {
                    RoutineScreen()
                }
                is Screen.ProfileScreen -> {
                    ProfileScreen()
                }
                is Screen.DoctorsScreen -> {
                    DoctorsScreen()
                }
                is Screen.DoctorFirstSignupScreen -> {
                    DoctorFirstSignupScreen()
                }
                is Screen.DoctorSecondSignupScreen -> {
                    DoctorSecondSignupScreen()
                }
                is Screen.DoctorLoginScreen -> {
                    DoctorLoginScreen()
                }
                is Screen.DoctorProfileScreen -> {
                    DoctorProfileScreen()
                }
            }
        }
    }
}