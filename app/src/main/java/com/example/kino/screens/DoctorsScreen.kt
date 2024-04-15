package com.example.kino.screens

import com.example.kino.rules.doctorList.DoctorsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.DoctorCard
import com.example.kino.components.NavigationAppBar
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun DoctorsScreen(navigationViewModel: NavigationViewModel = viewModel(), doctorsViewModel: DoctorsViewModel = viewModel()) {
    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationViewModel = navigationViewModel, pageIndex = navigationViewModel.navigationItemsList[0].index)
        },
        topBar = {
            AppToolbar(
                toolbarTitle = stringResource(id = R.string.chats)
            )
        },
    ) { paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            val users by doctorsViewModel.users.observeAsState(initial = emptyList())
            LazyColumn {
                items(users) { user ->
                    DoctorCard(user = user)
                }
            }

        }
    }
}


@Preview
@Composable
fun abdcv(){
    //DoctorsScreen()
}