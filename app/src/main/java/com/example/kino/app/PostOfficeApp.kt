package com.example.kino.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.screens.ChatsScreen
import com.example.kino.screens.DoctorLoginScreen
import com.example.kino.screens.DoctorFirstSignupScreen
import com.example.kino.screens.DoctorProfileScreen
import com.example.kino.screens.DoctorSecondSignupScreen
import com.example.kino.screens.DoctorsScreen
import com.example.kino.screens.HomeScreen
import com.example.kino.screens.LoginScreen
import com.example.kino.screens.ProfileScreen
import com.example.kino.screens.RoutineScreen
import com.example.kino.screens.SignUpScreen
import com.example.kino.screens.TermnAndConditionsSceen

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