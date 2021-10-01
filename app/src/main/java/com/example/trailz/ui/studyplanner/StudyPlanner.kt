package com.example.trailz.ui.studyplanner

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch

@Composable
fun StudyPlan(
    viewModel: StudyPlannerViewModel,
    navigateUp: () -> Unit
){
    val studyPlan by viewModel.studyPlan.observeAsState(initial = StudyPlanUiModel(loading = true))
    StudyPlan(
        studyPlan = studyPlan,
        navigateUp = navigateUp
    )
}

@Composable
fun StudyPlan(
    studyPlan: StudyPlanUiModel,
    navigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Study Plan") },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.ModeEdit, contentDescription = null)
                    }
                }
            )
        }
    ){
        studyPlan.studyPlan?.let {
            Column {
                Text(text = it.userId)
                Text(text = it.title)
                it.semesters.forEach {
                    Text(text = "Semester #${it.order}")
                    it.courses.forEach {
                        Text(text = it.title)
                    }
                }
            }
        }

        studyPlan.error?.let { errorMsg ->
            Text(text = errorMsg)
        }

        if (studyPlan.loading){
            CircularProgressIndicator()
        }
    }
}

