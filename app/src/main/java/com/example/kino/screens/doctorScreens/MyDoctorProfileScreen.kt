package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.kino.components.ClickableReviewTextComponent
import com.example.kino.components.DoctorDetailsCard
import com.example.kino.components.DoctorsReviewList
import com.example.kino.components.NavigationAppBar
import com.example.kino.components.ProfileImage
import com.example.kino.components.ReviewList
import com.example.kino.components.UserDetailsCard
import com.example.kino.rules.doctorProfile.DoctorProfileUIEvent
import com.example.kino.rules.myDoctorProfile.MyDoctorProfileViewModel
import com.example.kino.rules.navigation.NavigationViewModel
import com.example.kino.rules.user.UserUIEvent
import com.example.kino.rules.user.UserViewModel

@Composable
fun MyDoctorProfileScreen(navigationViewModel: NavigationViewModel = viewModel(), myDoctorProfileViewModel: MyDoctorProfileViewModel = viewModel()){
    val user by myDoctorProfileViewModel.user.collectAsState()
    val reviews = myDoctorProfileViewModel.reviews.collectAsState().value
    val nrReview = myDoctorProfileViewModel.sizeReviews.value.toString()
    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationItems = navigationViewModel.navigationItemsListD, pageIndex = 2)
        },
        topBar = {
            AppToolbar(
                toolbarTitle = stringResource(id = R.string.my_profile),
                isDoctor = true
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
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                Box(contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize())
                {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileImage(user?.imageUrl)
                        Spacer(modifier = Modifier.height(10.dp))
                        DoctorDetailsCard(stringResource(id = R.string.name), "${user?.lastName} ${user?.firstName}")
                        DoctorDetailsCard(stringResource(id = R.string.email), user?.email)
                        DoctorDetailsCard(stringResource(id = R.string.workplace), user?.workplace)
                        DoctorDetailsCard(stringResource(id = R.string.years_of_experience), user?.yearsOfExperience.toString())
                        DoctorDetailsCard(stringResource(id = R.string.university), user?.university)
                        ClickableReviewTextComponent(
                            nrReview,
                            { myDoctorProfileViewModel.seeReviewsClicked() })
                        DoctorsReviewList(reviews)
                        ButtonComponent(
                            value = stringResource(id = R.string.logout),
                            onButtonClicked = { myDoctorProfileViewModel.logout() },
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
}}}