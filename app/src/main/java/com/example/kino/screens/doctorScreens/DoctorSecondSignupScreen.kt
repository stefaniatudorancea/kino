package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.ButtonComponent
import com.example.kino.components.CheckboxComponent
import com.example.kino.components.ClickableLoginTextComponent
import com.example.kino.components.DividerTextComponent
import com.example.kino.components.HeadingTextComponent
import com.example.kino.components.MyTextFieldComponent
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.doctorSignup.DoctorSignupUIEvent
import com.example.kino.rules.doctorSignup.DoctorSignupViewModel

@Composable
fun DoctorSecondSignupScreen(doctorSignupViewModel: DoctorSignupViewModel = viewModel()){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                HeadingTextComponent(value = stringResource(id = R.string.create_account))
                Spacer(modifier = Modifier.height(20.dp))
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.phone_number),
                    painterResource = painterResource(id = R.drawable.telephone),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.PhoneNumberChanged(it))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.phoneNumberError,
                    fieldForNumbers = true
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.years_of_experience),
                    painterResource = painterResource(id = R.drawable.work),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.YearsOfExperienceChanged(it.toInt()))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.yearsOfExperienceError,
                    fieldForNumbers = true
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.workplace),
                    painterResource = painterResource(id = R.drawable.work),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.WorkplaceChanged(it))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.workplaceError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.university),
                    painterResource = painterResource(id = R.drawable.education),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.UniversityChanged(it))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.universityError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.graduation_year),
                    painterResource = painterResource(id = R.drawable.education),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.GraduationYearChanged(it.toInt()))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.graduationYearError,
                    fieldForNumbers = true
                )


//                MyTextFieldComponent(
//                    labelValue = stringResource(id = R.string.linkedln),
//                    painterResource = painterResource(id = R.drawable.social),
//                    onTextSelected = {
//                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.LinkedlnChanged(it))
//                    },
//                    errorStatus = doctorSignupViewModel.registartionUIState.value.linkedlnError
//                )
                CheckboxComponent(
                    onTextSelected = {
                        PostOfficeAppRouter.navigateTo(Screen.TermsAndConditionsScreen)
                    },
                    onCheckedChange = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.PrivacyPolicyCheckBoxClicked(it))
                    },
                )
                Spacer(modifier = Modifier.height(55.dp))
                Spacer(modifier = Modifier.height(65.dp))
                ButtonComponent(value = stringResource(id = R.string.register),
                    onButtonClicked = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.RegisterButtonClicked )
                    },
                    isEnabled = doctorSignupViewModel.secondValidationsPassed.value,
                    imageVector = null,
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.primaryBlue),
                            colorResource(id = R.color.secondaryBlue),
                        )
                    )

                )
                DividerTextComponent(stringResource(id = R.string.or))
                ClickableLoginTextComponent(onTextSelected = {
                    PostOfficeAppRouter.navigateTo(Screen.DoctorLoginScreen)
                })
                Spacer(modifier = Modifier.height(47.dp))
                ButtonComponent(value = stringResource(id = R.string.im_patient),
                    onButtonClicked = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.PatientButtonClicked )
                    },
                    isEnabled = true,
                    imageVector = null,
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.primaryPurple),
                            colorResource(id = R.color.secondaryPurple),
                        )
                    ),
                )
            }
        }
    }
}