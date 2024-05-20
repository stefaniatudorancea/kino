package com.example.kino.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.kino.R
import com.example.kino.data.RoutineExerciseData
import com.example.kino.data.RoutinePatientExerciseData


@Composable
fun RoutineCard(routineName: String, numberOfDoneExercises: Int,  numberOfExercises: Int, onStart: () -> Unit) {
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
                Text("START")
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
        Column(
            modifier = Modifier.padding(7.dp)
        ) {
            Text(
                text = "${ex.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${ex.description}",
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
    }
}
