package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudyPlanner(
    openMarketplace: () -> Unit
){
    Column {
        Text(text = "Study Planner2")
        Button(onClick = openMarketplace) {
            Text(text = "Open marketplace")
        }
    }
}

