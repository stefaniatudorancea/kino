package com.example.kino.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.kino.components.ClickableLoginTextComponent
import com.example.kino.components.DividerTextComponent
import com.example.kino.components.HeadingTextComponent
import com.example.kino.components.MyTextFieldComponent
import com.example.kino.components.PasswordTextFieldComponent
import com.example.kino.components.UnderLinedTextComponent
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.doctorLogin.DoctorLoginUIEvent
import com.example.kino.rules.doctorLogin.DoctorLoginViewModel
import com.example.kino.rules.login.LoginUIEvent
import com.example.kino.rules.login.LoginViewModel
@Composable
fun DoctorLoginScreen(doctorLoginViewModel: DoctorLoginViewModel = viewModel()) {
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
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(20.dp))
                HeadingTextComponent(value = stringResource(id = R.string.welcome))
                Spacer(modifier = Modifier.height(120.dp))
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.envelope),
                    onTextSelected = {doctorLoginViewModel.onEvent(DoctorLoginUIEvent.EmailChanged(it))},
                    errorStatus = doctorLoginViewModel.loginUIState.value.emailError
                )
                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.lock),
                    onTextSelected = {doctorLoginViewModel.onEvent(DoctorLoginUIEvent.PasswordChanged(it))},
                    errorStatus = doctorLoginViewModel.loginUIState.value.passwordError
                )
                Spacer(modifier = Modifier.height(40.dp))
                UnderLinedTextComponent(value = stringResource(id = R.string.forgot_password))
                Spacer(modifier = Modifier.height(40.dp))
                ButtonComponent(
                    value = stringResource(id = R.string.login),
                    onButtonClicked = {
                        doctorLoginViewModel.onEvent(
                            DoctorLoginUIEvent.LoginButtonClicked
                        )
                    },
                    isEnabled = doctorLoginViewModel.allValidationsPassed.value,
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.primaryBlue),
                            colorResource(id = R.color.secondaryBlue),
                        )
                    ),
                    imageVector = null
                )
                Spacer(modifier = Modifier.height(20.dp))
                DividerTextComponent(stringResource(id = R.string.or))
                ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                    PostOfficeAppRouter.navigateTo(Screen.DoctorFirstSignupScreen)
                })
                Spacer(modifier = Modifier.height(165.dp))

                ButtonComponent(
                    value = stringResource(id = R.string.im_patient),
                    onButtonClicked = {
                        doctorLoginViewModel.onEvent(DoctorLoginUIEvent.PatientButtonClicked)
                    },
                    isEnabled = true,
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.primaryPurple),
                            colorResource(id = R.color.secondaryPurple),
                        )
                    ),
                    imageVector = null
                )
            }
        }

        if(doctorLoginViewModel.loginInProcess.value){
            CircularProgressIndicator()
        }

    }

}