package com.example.kino.screens.doctorScreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.kino.components.ImageViewer
import com.example.kino.components.MyTextFieldComponent
import com.example.kino.components.PasswordTextFieldComponent
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.doctorSignup.DoctorSignupUIEvent
import com.example.kino.rules.doctorSignup.DoctorSignupViewModel

@Composable
fun DoctorFirstSignupScreen(doctorSignupViewModel: DoctorSignupViewModel = viewModel()){
    val imageUri by doctorSignupViewModel.imageUri.observeAsState()
    val imageUrl by doctorSignupViewModel.imageUrl.observeAsState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            doctorSignupViewModel.setImageUri(uri)
        }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeadingTextComponent(value = stringResource(id = R.string.create_account))
                Spacer(modifier = Modifier.height(20.dp))
                ImageViewer(imageUri = imageUri, imageUrl = imageUrl, {imagePickerLauncher.launch("image/*")})
                Spacer(modifier = Modifier.height(20.dp))
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.first_name),
                    painterResource = painterResource(id = R.drawable.user),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.FirstNameChanged(it))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.firstNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.last_name),
                    painterResource = painterResource(id = R.drawable.user),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.LastNameChanged(it))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.lastNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.envelope),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.EmailChanged(it))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.emailError
                )
                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.lock),
                    onTextSelected = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.PasswordChanged(it))
                    },
                    errorStatus = doctorSignupViewModel.registartionUIState.value.passwordError
                )
                Spacer(modifier = Modifier.height(40.dp))
                ButtonComponent(value = stringResource(id = R.string.next_step),
                    onButtonClicked = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.NextStepButtonClicked )
                    },
                    isEnabled = doctorSignupViewModel.firstValidationsPassed.value,
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.primaryBlue),
                            colorResource(id = R.color.secondaryBlue),
                        )
                    ),
                    imageVector = Icons.Filled.ArrowForward
                )
                DividerTextComponent(stringResource(id = R.string.or))
                ClickableLoginTextComponent(onTextSelected = {
                    PostOfficeAppRouter.navigateTo(Screen.DoctorLoginScreen)
                })
                Spacer(modifier = Modifier.height(116.dp))
                ButtonComponent(value = stringResource(id = R.string.im_patient),
                    onButtonClicked = {
                        doctorSignupViewModel.onEvent(DoctorSignupUIEvent.PatientButtonClicked)
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
    }
}