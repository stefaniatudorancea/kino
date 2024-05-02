package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.NavigationAppBar
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun AssignRoutinesScreen(navigationViewModel: NavigationViewModel = viewModel()){
    Scaffold(
        topBar = {
            AppToolbar(toolbarTitle = stringResource(id = R.string.list_of_routines), isDoctor = true)
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
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(horizontal = 0.dp, vertical = 10.dp)
                        .padding(top = 3.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    BackButton()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(horizontal = 0.dp, vertical = 10.dp)
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonComponent(
                        value = stringResource(id = R.string.create_a_routine),
                        onButtonClicked = { PostOfficeAppRouter.navigateTo(Screen.AssignRoutinesScreen) },
                        brush = Brush.horizontalGradient(
                            listOf(
                                colorResource(id = R.color.primaryBlue),
                                colorResource(id = R.color.secondaryBlue),
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