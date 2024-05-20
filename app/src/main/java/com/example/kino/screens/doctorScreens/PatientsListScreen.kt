package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.NavigationAppBar
import com.example.kino.components.PatientCard
import com.example.kino.rules.navigation.NavigationViewModel
import com.example.kino.rules.patientsList.PatientsListViewModel

@Composable
fun PatientsListScreen(navigationViewModel: NavigationViewModel = viewModel(), patientsListViewModel: PatientsListViewModel = viewModel()){
    Scaffold(
        bottomBar = {
            NavigationAppBar(
                navigationItems = navigationViewModel.navigationItemsListD,
                pageIndex = navigationViewModel.navigationItemsListD[0].index
            )
        },
        topBar = {
            AppToolbar(toolbarTitle = stringResource(id = R.string.patients), isDoctor = true)
        },
    ){paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            val patientsList by patientsListViewModel.patientsList.collectAsState()
            LazyColumn {
                patientsList?.let { list ->
                    items(list) { patient ->
                        PatientCard(user = patient)
                    }
                } ?: item {
                    Text("Niciun pacient disponibil")
                }
            }
        }

    }

    if(patientsListViewModel.fetchPatiensProcess.value){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }
}