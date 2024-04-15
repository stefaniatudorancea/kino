package com.example.kino.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
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
import com.example.kino.rules.navigation.NavigationViewModel
import com.example.kino.rules.user.UserUIEvent
import com.example.kino.rules.user.UserViewModel

@Composable
fun ProfileScreen(navigationViewModel: NavigationViewModel = viewModel(), userViewModel: UserViewModel = viewModel()){
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
                    ProfileImage(userViewModel.user.value?.imageUrl)
                    Spacer(modifier = Modifier.height(10.dp))
                    UserDetailsCard(stringResource(id = R.string.name), "${userViewModel.user.value?.lastName} ${userViewModel.user.value?.firstName}")
                    UserDetailsCard(stringResource(id = R.string.email), userViewModel.user.value?.email)
                    ButtonComponent(
                        value = stringResource(id = R.string.logout),
                        onButtonClicked = { userViewModel.onEvent(UserUIEvent.LogoutButtonClicked) },
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