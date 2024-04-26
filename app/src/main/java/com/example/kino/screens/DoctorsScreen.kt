package com.example.kino.screens

import com.example.kino.rules.doctorList.DoctorsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.DividerTextComponent
import com.example.kino.components.DoctorCard
import com.example.kino.components.FavDoctorCard
import com.example.kino.components.NavigationAppBar
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun DoctorsScreen(navigationViewModel: NavigationViewModel = viewModel(), doctorsViewModel: DoctorsViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        // Acest apel va reîmprospăta datele ori de câte ori intri pe acest ecran
        doctorsViewModel.fetchUsers()
    }
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
            val favDoctor by doctorsViewModel.favDoctor.observeAsState()
            LazyColumn {
                // Adaugă medicul favorit ca un header al listei
                item {
                    favDoctor?.let {
                        DividerTextComponent(stringResource(id = R.string.your_physiotherapist))
                        DoctorCard(user = it)
                        DividerTextComponent(stringResource(id = R.string.other_physiotherapists))
                    }
                }

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
    DoctorsScreen()
}