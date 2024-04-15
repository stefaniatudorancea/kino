package com.example.kino.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.ButtonComponent
import com.example.kino.components.NavigationAppBar
import com.example.kino.components.ProfileImage
import com.example.kino.components.UserDetailsCard
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.doctorList.DoctorsViewModel
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun DoctorProfileScreen(navigationViewModel: NavigationViewModel = viewModel(), doctorsViewModel: DoctorsViewModel = viewModel(), chatViewModel: ChatViewModel = viewModel()){
    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationViewModel = navigationViewModel, pageIndex = null)
        },
        topBar = {
            AppToolbar(
                toolbarTitle = stringResource(id = R.string.doctor_profile)
            )
        },
    ){
        paddingValues ->
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
            ){
                Box(contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize())
                {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(20.dp))
                        ProfileImage(doctorsViewModel.selectedDoctor.value?.imageUrl)
                        Spacer(modifier = Modifier.height(10.dp))
                        UserDetailsCard(stringResource(id = R.string.name), "${doctorsViewModel.selectedDoctor.value?.lastName} ${doctorsViewModel.selectedDoctor.value?.firstName}")
                        UserDetailsCard(stringResource(id = R.string.workplace), doctorsViewModel.selectedDoctor.value?.workplace)
                        UserDetailsCard(stringResource(id = R.string.years_of_experience), doctorsViewModel.selectedDoctor.value?.yearsOfExperience.toString())
                        UserDetailsCard(stringResource(id = R.string.university), doctorsViewModel.selectedDoctor.value?.university)
                        UserDetailsCard(stringResource(id = R.string.email), doctorsViewModel.selectedDoctor.value?.email)
                        Spacer(modifier = Modifier.height(10.dp))
//                        ButtonComponent(
//                            value = stringResource(id = R.string.write_message),
//                            onButtonClicked = { chatViewModel.initiateChatWithDoctor(doctorsViewModel.selectedDoctor.value!!.uid) },
//                            brush = Brush.horizontalGradient(
//                                listOf(
//                                    colorResource(id = R.color.primaryPurple),
//                                    colorResource(id = R.color.secondaryPurple),
//                                )
//                            ),
//                            imageVector = null,
//                            isEnabled = true
//                        )
                        ButtonComponent(
                            value = stringResource(id = R.string.try_collab),
                            onButtonClicked = { chatViewModel.initiateChatWithDoctor(doctorsViewModel.selectedDoctor.value!!.uid) },
                            brush = Brush.horizontalGradient(
                                listOf(
                                    colorResource(id = R.color.primaryPurple),
                                    colorResource(id = R.color.secondaryPurple),
                                )
                            ),
                            imageVector = null,
                            isEnabled = true
                        )
                    }
                }

            }
    }
}