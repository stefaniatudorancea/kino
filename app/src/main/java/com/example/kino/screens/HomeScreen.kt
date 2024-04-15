package com.example.kino.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.NavigationAppBar
import com.example.kino.rules.navigation.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigationViewModel: NavigationViewModel = viewModel()) {


    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationViewModel = navigationViewModel, pageIndex = navigationViewModel.navigationItemsList[0].index)
        },
        topBar = {
            AppToolbar(
                toolbarTitle = stringResource(id = R.string.home))
        },
    ) { paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {

        }
    }


}

@Preview
@Composable
fun HomeScreenPr() {
    HomeScreen()
}