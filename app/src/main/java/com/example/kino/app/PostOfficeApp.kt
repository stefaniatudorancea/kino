package com.example.kino.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.screens.doctorScreens.AssignRoutinesScreen
import com.example.kino.screens.doctorScreens.CreateExerciseScreen
import com.example.kino.screens.doctorScreens.CreateRoutineScreen
import com.example.kino.screens.doctorScreens.DoctorChatScreen
import com.example.kino.screens.patientScreens.ChatsScreen
import com.example.kino.screens.doctorScreens.DoctorLoginScreen
import com.example.kino.screens.doctorScreens.DoctorFirstSignupScreen
import com.example.kino.screens.patientScreens.DoctorProfileScreen
import com.example.kino.screens.doctorScreens.DoctorSecondSignupScreen
import com.example.kino.screens.doctorScreens.ExerciseScreen
import com.example.kino.screens.doctorScreens.ExercisesListScreen
import com.example.kino.screens.doctorScreens.MyDoctorProfileScreen
import com.example.kino.screens.doctorScreens.PatientsListScreen
import com.example.kino.screens.doctorScreens.ProfileDoctorScreen
import com.example.kino.screens.doctorScreens.RoutineScreen
import com.example.kino.screens.doctorScreens.RoutinesListScreen
import com.example.kino.screens.patientScreens.DoctorsScreen
import com.example.kino.screens.patientScreens.HomeScreen
import com.example.kino.screens.patientScreens.LoginScreen
import com.example.kino.screens.patientScreens.PatientExerciseScreen
import com.example.kino.screens.patientScreens.PatientRoutineScreen
import com.example.kino.screens.patientScreens.ProfileScreen
import com.example.kino.screens.patientScreens.SignUpScreen
import com.example.kino.screens.patientScreens.TermnAndConditionsSceen

@RequiresApi(Build.VERSION_CODES.O)
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
                is Screen.ProfileDoctorScreen -> {
                    ProfileDoctorScreen()
                }
                is Screen.DoctorProfileScreen -> {
                    DoctorProfileScreen()
                }
                is Screen.PatientsListScreen -> {
                    PatientsListScreen()
                }
                is Screen.RoutinesListScreen -> {
                    RoutinesListScreen()
                }
                is Screen.DoctorChatScreen -> {
                    DoctorChatScreen()
                }
                is Screen.AssignRoutinesScreen -> {
                    AssignRoutinesScreen()
                }
                is Screen.ExercisesListScreen -> {
                    ExercisesListScreen()
                }
                is Screen.CreateExerciseScreen -> {
                    CreateExerciseScreen()
                }
                is Screen.CreateRoutineScreen -> {
                    CreateRoutineScreen()
                }
                is Screen.ExerciseScreen -> {
                    ExerciseScreen()
                }
                is Screen.PatientExerciseScreen -> {
                    PatientExerciseScreen()
                }
                is Screen.PatientRoutineScreen -> {
                    PatientRoutineScreen()
                }
                is Screen.MyDoctorProfileScreen -> {
                    MyDoctorProfileScreen()
                }
            }
        }
    }
}