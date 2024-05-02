package com.example.kino.screens.patientScreens

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
import com.example.kino.components.FavDoctorDialog
import com.example.kino.components.NavigationAppBar
import com.example.kino.components.ProfileImage
import com.example.kino.components.UserDetailsCard
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.doctorProfile.DoctorProfileViewModel
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun DoctorProfileScreen(navigationViewModel: NavigationViewModel = viewModel(), doctorProfileViewModel: DoctorProfileViewModel = viewModel(), chatViewModel: ChatViewModel = viewModel()){
    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationItems = navigationViewModel.navigationItemsList, pageIndex = null)
        },
        topBar = {
            AppToolbar(
                toolbarTitle = stringResource(id = R.string.doctor_profile),
                isDoctor = false
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
                    FavDoctorDialog()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(20.dp))
                        ProfileImage(doctorProfileViewModel.selectedDoctor.value?.imageUrl)
                        Spacer(modifier = Modifier.height(10.dp))
                        UserDetailsCard(stringResource(id = R.string.name), "${doctorProfileViewModel.selectedDoctor.value?.lastName} ${doctorProfileViewModel.selectedDoctor.value?.firstName}")
                        UserDetailsCard(stringResource(id = R.string.workplace), doctorProfileViewModel.selectedDoctor.value?.workplace)
                        UserDetailsCard(stringResource(id = R.string.years_of_experience), doctorProfileViewModel.selectedDoctor.value?.yearsOfExperience.toString())
                        UserDetailsCard(stringResource(id = R.string.university), doctorProfileViewModel.selectedDoctor.value?.university)
                        UserDetailsCard(stringResource(id = R.string.email), doctorProfileViewModel.selectedDoctor.value?.email)
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
                            onButtonClicked = { doctorProfileViewModel.addFavDoctor() },
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