package com.example.kino.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.screens.HomeScreen
import com.example.kino.screens.LoginScreen
import com.example.kino.screens.SignUpScreen
import com.example.kino.screens.TermnAndConditionsSceen

@Composable
fun PostOfficeApp(){
    Surface(modifier = Modifier.fillMaxSize(),
        color = Color.White){

        Crossfade(targetState = PostOfficeAppRouter.currentScreen){ currentState->
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
            }
        }
    }
}