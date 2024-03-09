package com.example.kino.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kino.R
import com.example.kino.components.ButtonComponent
import com.example.kino.components.ClickableLoginTextComponent
import com.example.kino.components.DividerTextComponent
import com.example.kino.components.HeadingTextComponent
import com.example.kino.components.MyTextFieldComponent
import com.example.kino.components.NormalTextComponent
import com.example.kino.components.PasswordTextFieldComponent
import com.example.kino.components.UnderLinedTextComponent
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen

@Composable
fun LoginScreen(){
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ){
        Column(modifier = Modifier.fillMaxSize()){
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.welcome))
            Spacer(modifier = Modifier.height(20.dp))
            MyTextFieldComponent(labelValue = stringResource(id = R.string.email), painterResource = painterResource(id = R.drawable.envelope))
            PasswordTextFieldComponent(labelValue = stringResource(id = R.string.password), painterResource = painterResource(id = R.drawable.lock))
            Spacer(modifier = Modifier.height(40.dp))
            UnderLinedTextComponent(value = stringResource(id = R.string.forgot_password))
            Spacer(modifier = Modifier.height(40.dp))
            ButtonComponent(value = stringResource(id = R.string.login))
            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                PostOfficeAppRouter.navigateTo(Screen.SignUpScreen)
            })
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview(){
    LoginScreen()
}