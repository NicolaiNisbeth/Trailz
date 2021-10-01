package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun StudyPlan(
    viewModel: StudyPlannerViewModel,
    openMarketplace: () -> Unit
){
    val studyPlan by viewModel.studyPlan.observeAsState(initial = StudyPlanUiModel(loading = true))
    StudyPlan(
        studyPlan = studyPlan
    )
}

@Composable
fun StudyPlan(
    studyPlan: StudyPlanUiModel
) {
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

