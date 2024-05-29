package com.example.kino.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.data.DoctorData
import com.example.kino.data.DoctorReview
import com.example.kino.data.FeedbackData
import com.example.kino.data.Message
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.doctorProfile.DoctorProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChatTextField(
    onTextSelected: (String) -> Unit,
    onButtonClicked: () -> Unit,
    isEnabled: Boolean = false
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Adaugă padding pentru a nu lipi componentele de marginile ecranului
        verticalAlignment = Alignment.CenterVertically
    ) {
        var textValue = remember { mutableStateOf("") }
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = textValue.value,
            modifier = Modifier
                .weight(1f)  // Utilizează 'weight' pentru a ocupa tot spațiul disponibil minus butonul
                .padding(end = 8.dp),
            onValueChange = {
                textValue.value = it
                onTextSelected(it)
            },
            singleLine = true,
        )
        IconButton(
            onClick = { onButtonClicked.invoke(); textValue.value = ""; },
            enabled = isEnabled
        )
        {
            Icon(
                imageVector = Icons.Outlined.Send,
                contentDescription = "Send Message",
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReceivedMessage(message: Message, currentUser: String) {
    Box(
        contentAlignment = if (message.senderId == currentUser) Alignment.CenterEnd else Alignment.CenterStart,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.senderId == currentUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(
                    min = 10.dp,
                    max = 300.dp
                )  // Limităm lățimea cardului între 100.dp și 300.dp
                .wrapContentHeight()  // Înălțimea se adaptează la conținut
        ) {
            Column {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier

                        .padding(horizontal = 10.dp, vertical = 6.dp)  // Padding intern pentru text
                )
                Text(
                    text = extractHourFromTimestampString(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 0.dp, end = 0.dp, bottom = 10.dp)  // Padding intern pentru text
                )
            }
        }
    }
}

@Composable
fun FeedbackRoutineDialog(feedbacks: List<FeedbackData>, showDialog: Boolean, onCloseDialog: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onCloseDialog() },
            title = {  },
            text = {
                FeedbackRoutineList(feedbacks)
            },
            confirmButton = {
                Button(
                    onClick = { onCloseDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue)),
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun FeedbackRoutineList(feedbacks: List<FeedbackData>) {
        Column {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .heightIn(max = 250.dp)
            ) {
                Column {
                    LazyColumn {
                        items(feedbacks.size) { index ->
                            FeedbackRoutineItem(feedbacks[index])
                        }
                    }
                }

            }
        }
    }


@Composable
fun FeedbackRoutineItem(feedback: FeedbackData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cardBlue))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "${feedback.exerciseName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = "${feedback.feedback}")
        }
    }
}

@Composable
fun ClickableFeedbackTextComponent(onTextSelected: () -> Unit) {
    val text = stringResource(id = R.string.view_feedback)
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = colorResource(id = R.color.buttonBlue), textDecoration = TextDecoration.Underline)) {
            pushStringAnnotation(tag = "action", annotation = "viewFeedback")
            append(text)
            pop()  // Make sure to pop after appending to end the annotation
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
            annotatedString.getStringAnnotations(tag = "action", start = offset, end = offset).firstOrNull()?.let {
                if (it.item == "viewFeedback") {
                    onTextSelected()
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun extractHourFromTimestampString(timestampString: String): String {
    val timestamp = timestampString.toLong()
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(localDateTime)
}

@Composable
fun RoutineDoctorCard(routineName: String, numberOfDoneExercises: Long,  numberOfExercises: Int) {
    Card(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 40.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cardBlue)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = routineName,
                style = MaterialTheme.typography.headlineSmall, // Actualizat pentru a folosi tipografia Material 3
                modifier = Modifier.padding(bottom = 8.dp) // Un mic padding pentru separație
            )
            Text(
                text = "Number of exercises: $numberOfDoneExercises/$numberOfExercises",
                style = MaterialTheme.typography.bodyMedium // Actualizat pentru a folosi tipografia Material 3
            )
        }
    }
}




