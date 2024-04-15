package com.example.kino.components


import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.kino.R

@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )
}

@Composable
fun MyTextFieldComponent(
    labelValue: String,
    painterResource: Painter?,
    onTextSelected: (String) -> Unit,
    errorStatus: Boolean = false,
    fieldForNumbers: Boolean = false
) {
    val textValue = remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small),
        shape = RoundedCornerShape(50),
        value = textValue.value,
        label = { Text(text = labelValue) },
        onValueChange = {newValue ->
            if(!fieldForNumbers){
                textValue.value = newValue
            }else{
                textValue.value = newValue.filter { it.isDigit() }
            }
            onTextSelected(newValue)
        },
        //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines= 1,
        leadingIcon = {
            painterResource?.let {
                Icon(
                    modifier = Modifier.height(10.dp),
                    painter = it,
                    contentDescription = ""
                )
            }
        },
        isError = !errorStatus,

    )
}

@Composable
fun PasswordTextFieldComponent(labelValue: String, painterResource: Painter, onTextSelected: (String) -> Unit, errorStatus: Boolean = false) {
    val password = remember { mutableStateOf("") }
    val passwordVisibile = remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small),
        shape = RoundedCornerShape(50),
        value = password.value,
        label = { Text(text = labelValue) },
        onValueChange = { password.value = it
                        onTextSelected(it)},
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.Password,
//            imeAction = ImeAction.Done
//        ),
        singleLine = true,
        maxLines = 1,
//        keyboardActions = KeyboardActions {
//            localFocusManager.clearFocus()
//        },
        leadingIcon = {
            Icon(
                modifier = Modifier.height(10.dp),
                painter = painterResource,
                contentDescription = ""
            )
        },
        trailingIcon = {
            val iconImage = if (passwordVisibile.value) {
                Icons.Filled.Visibility
                //painterResource(id = R.drawable.eye)
            } else {
                Icons.Filled.VisibilityOff
            }

            IconButton(onClick = { passwordVisibile.value = !passwordVisibile.value }) {
                Icon(imageVector = iconImage, contentDescription = null)
            }
        },

        visualTransformation = if (passwordVisibile.value) VisualTransformation.None else PasswordVisualTransformation(),
        isError = !errorStatus
    )
}

@Composable
fun CheckboxComponent(onTextSelected: (String) -> Unit, onCheckedChange: (Boolean)-> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val checkedState = remember {
            mutableStateOf(false)
        }
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = !checkedState.value
                onCheckedChange.invoke(it)
            })
        ClickableTextComponent(onTextSelected = onTextSelected)
    }
}

@Composable
fun ClickableTextComponent(onTextSelected: (String) -> Unit) {
    val initialText = "By continuing you accept our "
    val privacyPolicyText = "Privacy Policy "
    val andText = "and"
    val termAndConditionText = " Terms of Use"
    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = colorResource(id = R.color.purple_200))) {
            pushStringAnnotation(tag = privacyPolicyText, annotation = privacyPolicyText)
            append(privacyPolicyText)
        }
        append(andText)
        withStyle(style = SpanStyle(color = colorResource(id = R.color.purple_200))) {
            pushStringAnnotation(tag = termAndConditionText, annotation = termAndConditionText)
            append(termAndConditionText)
        }
    }
    ClickableText(text = annotatedString, onClick = { offset ->
        annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.also { span ->
            Log.d("ClickableTextComponent", "{$span.item}")

            if (span.item == termAndConditionText || span.item == privacyPolicyText) {
                onTextSelected(span.item)
            }
        }
    })
}

@Composable
fun ButtonComponent(value: String, onButtonClicked: () -> Unit, isEnabled: Boolean = false, brush: Brush, imageVector: ImageVector?) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .padding(horizontal = 40.dp),
        contentPadding = PaddingValues(),
        onClick = {
            onButtonClicked.invoke()
        },
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        enabled = isEnabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(48.dp)
                .background(
                    brush = brush,
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (imageVector != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(imageVector = imageVector, contentDescription = "Next page", tint = Color.White)
                }
            }

        }
    }
}

@Composable
fun DividerTextComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            thickness = 1.dp,
            color = Color.Gray
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.or),
            fontSize = 14.sp,
            color = colorResource(id = R.color.colorText)
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

@Composable
fun ClickableLoginTextComponent(tryingToLogin: Boolean = true, onTextSelected: (String) -> Unit) {


    val initialText =
        if (tryingToLogin) "Already have an accout? " else "Don't have an account yet? "
    val loginText = if (tryingToLogin) "Login" else "Register"
    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = colorResource(id = R.color.purple_200))) {
            pushStringAnnotation(tag = loginText, annotation = loginText)
            append(loginText)
        }
    }

    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        text = annotatedString,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.also { span ->
                Log.d("ClickableTextComponent", "{$span.item}")

                if (span.item == loginText) {
                    onTextSelected(span.item)
                }
            }
        })
}

@Composable
fun UnderLinedTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.colorGray),
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun ImageViewer(imageUri: Uri?, imageUrl: String?, onButtonClicked: () -> Unit) {
    val painter = if (imageUrl != null) rememberImagePainter(imageUrl)
    else if (imageUri != null) rememberImagePainter(imageUri)
    else painterResource(id = R.drawable.default_profile)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable(onClick = onButtonClicked),
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}


//@Composable
//fun ImageViewer(imageUri: Uri?, imageUrl: String?, onButtonClicked: () -> Unit) {
//    val painter = if (imageUrl != null) rememberImagePainter(imageUrl) else if (imageUri != null) rememberImagePainter(imageUri) else painterResource(id = R.drawable.default_profile)
//
//    Image(
//        painter = painter,
//        contentDescription = "Profile Image",
//        modifier = Modifier
//            .size(120.dp)
//            .clip(CircleShape)
//            .border(2.dp, Color.Gray, CircleShape),
//        contentScale = ContentScale.Crop
//
//    )
//}






