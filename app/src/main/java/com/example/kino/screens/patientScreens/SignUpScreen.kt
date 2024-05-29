package com.example.kino.screens.patientScreens

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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.components.CheckboxComponent
import com.example.kino.components.ClickableLoginTextComponent
import com.example.kino.components.DividerTextComponent
import com.example.kino.components.HeadingTextComponent
import com.example.kino.components.ImageViewer
import com.example.kino.components.MyTextFieldComponent
import com.example.kino.components.PasswordTextFieldComponent
import com.example.kino.rules.signup.SignupViewModel
import com.example.kino.rules.signup.SignupUIEvent

@Composable
fun SignUpScreen(signupViewModel: SignupViewModel = viewModel()) {
    val imageUri by signupViewModel.imageUri.observeAsState()
    val imageUrl by signupViewModel.imageUrl.observeAsState()
    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            signupViewModel.setImageUri(uri)
        }
    )
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
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                HeadingTextComponent(value = stringResource(id = R.string.create_account))
                Spacer(modifier = Modifier.height(20.dp))
                ImageViewer(imageUri = imageUri, imageUrl = imageUrl, {imagePickerLauncher.launch("image/*")})
                Spacer(modifier = Modifier.height(20.dp))
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.first_name),
                    painterResource = painterResource(id = R.drawable.user),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.FirstNameChanged(it))
                    },
                    errorStatus = signupViewModel.registartionUIState.value.firstNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.last_name),
                    painterResource = painterResource(id = R.drawable.user),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.LastNameChanged(it))
                    },
                    errorStatus = signupViewModel.registartionUIState.value.lastNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.envelope),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.EmailChanged(it))
                    },
                    errorStatus = signupViewModel.registartionUIState.value.emailError
                )
                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.lock),
                    onTextSelected = {
                        signupViewModel.onEvent(SignupUIEvent.PasswordChanged(it))
                    },
                    errorStatus = signupViewModel.registartionUIState.value.passwordError
                )
                CheckboxComponent(
                    onTextSelected = {
                        PostOfficeAppRouter.navigateTo(Screen.TermsAndConditionsScreen)
                    },
                    onCheckedChange = {
                        signupViewModel.onEvent(SignupUIEvent.PrivacyPolicyCheckBoxClicked(it))
                    },
                )
                Spacer(modifier = Modifier.height(30.dp))
                ButtonComponent(value = stringResource(id = R.string.register),
                    onButtonClicked = {
                        signupViewModel.onEvent(SignupUIEvent.RegisterButtonClicked )
                    },
                    isEnabled = signupViewModel.allValidationsPassed.value,
                    imageVector = null,
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.primaryPurple),
                            colorResource(id = R.color.secondaryPurple),
                        )
                    )

                    )
                //Spacer(modifier = Modifier.height(20.dp))
                DividerTextComponent(stringResource(id = R.string.or))
                ClickableLoginTextComponent(onTextSelected = {
                    PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
                })
                Spacer(modifier = Modifier.height(71.dp))
                ButtonComponent(value = stringResource(id = R.string.im_physiotherapist),
                    onButtonClicked = {
                        signupViewModel.onEvent(SignupUIEvent.PhysioteraphistButtonClicked )
                    },
                    isEnabled = true,
                    imageVector = null,
                    brush = Brush.horizontalGradient(
                        listOf(
                            colorResource(id = R.color.primaryBlue),
                            colorResource(id = R.color.secondaryBlue),
                        )
                    ),
                )
            }
        }

        if(signupViewModel.signUpInProgress.value){
            CircularProgressIndicator()
        }
    }
}