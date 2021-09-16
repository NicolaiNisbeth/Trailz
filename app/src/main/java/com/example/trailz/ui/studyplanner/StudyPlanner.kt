package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun StudyPlanner(
    openMarketplace: () -> Unit
){
    Column {
        Text(text = "Study Planner")
        Button(onClick = openMarketplace) {
            Text(text = "Open marketplace")
        }
    }
}
