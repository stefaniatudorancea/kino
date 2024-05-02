package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.NavigationAppBar
import com.example.kino.rules.navigation.NavigationViewModel
import com.example.kino.rules.patientsList.PatientsListViewModel

@Composable
fun ProfileDoctorScreen(navigationViewModel: NavigationViewModel = viewModel(), patientsListViewModel: PatientsListViewModel = viewModel()){
    Scaffold(
        bottomBar = {
            NavigationAppBar(
                navigationItems = navigationViewModel.navigationItemsListD,
                pageIndex = navigationViewModel.navigationItemsListD[2].index
            )
        },
        topBar = {
            AppToolbar(toolbarTitle = stringResource(id = R.string.my_profile), isDoctor = true)
        },
    ){paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            //val patients by patientsListViewModel.patientsList.observeAsState()
        }

    }
}