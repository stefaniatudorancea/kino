package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppTabBarRoutinesExercises
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.NavigationAppBar
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.navigation.NavigationViewModel
import com.example.kino.rules.patientsList.PatientsListViewModel

@Composable
fun RoutinesScreen(navigationViewModel: NavigationViewModel = viewModel(), patientsListViewModel: PatientsListViewModel = viewModel()) {
    Scaffold(
        bottomBar = {
            NavigationAppBar(
                navigationItems = navigationViewModel.navigationItemsListD,
                pageIndex = navigationViewModel.navigationItemsListD[1].index
            )
        },
        topBar = {
            AppTabBarRoutinesExercises(onTabSelected = { screen ->
                PostOfficeAppRouter.navigateTo(screen)
            })
        },
    ) { paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                val scrollState = rememberLazyListState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(horizontal = 0.dp, vertical = 10.dp)
                        .padding(top = 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    BackButton()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonComponent(
                        value = stringResource(id = R.string.create_a_routine),
                        onButtonClicked = { PostOfficeAppRouter.navigateTo(Screen.CreateRoutineScreen) },
                        brush = Brush.horizontalGradient(
                            listOf(
                                colorResource(id = R.color.buttonBlue),
                                colorResource(id = R.color.buttonBlue),
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
