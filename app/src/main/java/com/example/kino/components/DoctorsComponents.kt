package com.example.kino.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.kino.R
import com.example.kino.data.DoctorData
import com.example.kino.data.DoctorReview
import com.example.kino.rules.doctorProfile.DoctorProfileUIEvent
import com.example.kino.rules.doctorProfile.DoctorProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DoctorCard(doctorsViewModel: DoctorProfileViewModel = viewModel(), user: DoctorData) {
    Card(
        colors = CardDefaults.cardColors(colorResource(id = R.color.primaryPurple)),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            ImageForCard(imageUrl = user.imageUrl)
            Text(
                text = "${user.firstName} ${user.lastName}",
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { doctorsViewModel.selectDoctor(user) }) {
                Icon(painter = painterResource(id = R.drawable.more_details), contentDescription = "details",  tint = Color.White)
            }
        }
    }
}

@Composable
fun ImageForCard(imageUrl: String?) {
    val painter = if (!imageUrl.isNullOrEmpty()) {
        rememberImagePainter(
            data = imageUrl,
            builder = {
                placeholder(R.drawable.default_profile) // imagine afișată în timpul încărcării
                error(R.drawable.default_profile) // imagine afișată în caz de eroare
            }
        )
    } else {
        painterResource(id = R.drawable.default_profile)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape),
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


@Composable
fun FavDoctorDialog(doctorViewModel: DoctorProfileViewModel = viewModel()) {
    val showDialog = doctorViewModel.showDialog.collectAsState()
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { doctorViewModel.dismissDialog() },
            text = { Text(stringResource(id = R.string.confirmation_end_collaboration)) },
            confirmButton = {
                Button(onClick = { doctorViewModel.onEvent(DoctorProfileUIEvent.ConfirmDialogButtonClicked) }) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { doctorViewModel.dismissDialog() }) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewDialog(doctorViewModel: DoctorProfileViewModel = viewModel(), onTextSelected: (String) -> Unit) {
    val showReviewDialog = doctorViewModel.showReviewDialog.collectAsState()
    var textValue by remember { mutableStateOf("") }

    if (showReviewDialog.value) {
        AlertDialog(
            onDismissRequest = { doctorViewModel.dismissDialog() },
            title = { Text(text = stringResource(id = R.string.add_review)) },
            text = { OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 5.dp)
                    .clip(componentShapes.small),
                shape = RoundedCornerShape(50),
                value = textValue,
                onValueChange = { it: String ->
                  textValue = it
                    onTextSelected(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = false,
            )},
            confirmButton = {
                Button(onClick = { doctorViewModel.onEvent(DoctorProfileUIEvent.AddReviewButtonClicked) }) {
                    Text(text = stringResource(id = R.string.done))
                }
            },
            dismissButton = {
                Button(onClick = { doctorViewModel.dismissReviewDialog() }) {
                    Text(stringResource(id = R.string.close))
                }
            }
        )
    }
}

@Composable
fun ReviewItem(review: DoctorReview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.reviewCardPurple))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "${review.firstName} ${review.lastName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = "${review.textReview}")
            Text(text = "${convertTimestampToDate(review.timestamp.toLong())}", fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
fun ClickableReviewTextComponent(nrReviews: String, onTextSelected: (String) -> Unit) {
    val text = "${nrReviews} reviews"
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = colorResource(id = R.color.purple_200))) {
            pushStringAnnotation(tag = text, annotation = text)
            append(text)
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
                Log.d("ClickableReviewComponent", "{$span.item}")
                if (span.item == text) {
                    onTextSelected(span.item)
                }
            }
        })
}

@Composable
fun ReviewList(reviews: List<DoctorReview>, doctorProfileViewModel: DoctorProfileViewModel = viewModel()) {
    val seeReviews = doctorProfileViewModel.seeReviews.value
    if(seeReviews){
        Column {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .heightIn(max = 250.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Column {
                    DividerTextComponent(stringResource(id = R.string.physiotherapist_reviews))
                    LazyColumn {
                        items(reviews.size) { index ->
                            ReviewItem(reviews[index])
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

fun convertTimestampToDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    return format.format(date)
}



