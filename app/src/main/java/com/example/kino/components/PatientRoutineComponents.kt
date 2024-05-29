package com.example.kino.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.kino.R
import com.example.kino.data.RoutineExerciseData
import com.example.kino.data.RoutinePatientExerciseData


@Composable
fun RoutineCard(routineName: String, numberOfDoneExercises: Long,  numberOfExercises: Int, onStart: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 40.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, // Utilizează forma definită în tema material
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Elevation conform Material 3
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
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onStart,
                shape = MaterialTheme.shapes.small, // Buton cu forma rotunjită definită în tema material
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Utilizează schema de culori primare din Material 3
            ) {
                if(numberOfDoneExercises == 0L){
                    Text(text = stringResource(id = R.string.start))
                }else{
                    Text(text = stringResource(id = R.string.contin))
                }
            }
        }
    }
}

@Composable
fun SeePatientRoutineExerciseItem(ex: RoutinePatientExerciseData, onCardClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onCardClicked)
    ) {
        Row(
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // This spreads out the children across the horizontal axis
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ex.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                ex.description?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row {
                    Text(
                        text = "${stringResource(id = R.string.series)}: ${ex.series}",
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${stringResource(id = R.string.repetitions)}: ${ex.repetitions}",
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            val paint : Painter
            if(!ex.done) {
                paint = painterResource(id = R.drawable.more_details)
            }else{
                paint = painterResource(id = R.drawable.tick)
            }
            Icon(
                painter = paint,
                contentDescription = "details",
                modifier = Modifier.padding(4.dp).width(30.dp) // Optional padding for better touch target or visual spacing
            )
        }
    }
}

@Composable
fun RoutineFeedbackComponent(onTextSelected: (String) -> Unit){
    val textValue = remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 5.dp)
            .clip(componentShapes.small),
        shape = RoundedCornerShape(50),
        value = textValue.value,
        label = { Text(text = stringResource(id = R.string.feedback)) },
        onValueChange = {it ->
            textValue.value = it
            onTextSelected(it)
        },
        singleLine = false,
    )
}
