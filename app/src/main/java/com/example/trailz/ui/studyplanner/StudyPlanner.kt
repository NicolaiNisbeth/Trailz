package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trailz.ui.mystudyplan.CourseItemEdit
import com.example.trailz.ui.mystudyplan.CourseItemSave
import com.example.trailz.ui.mystudyplan.SemesterItemEdit
import com.example.trailz.ui.mystudyplan.SemesterItemSave
import com.example.trailz.ui.studyplanners.StudyPlan
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun StudyPlanner(
    viewModel: StudyPlannerViewModel,
    navigateUp: () -> Unit,
){
    val studyPlan by viewModel.studyPlan.observeAsState(initial = StudyPlanUiModel())
    StudyPlanner(
        studyPlan = studyPlan,
        navigateUp = navigateUp,
    )
}

@ExperimentalFoundationApi
@Composable
fun StudyPlanner(
    studyPlan: StudyPlanUiModel,
    navigateUp: () -> Unit,
){
    val (studyPlan, error, loading) = studyPlan

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
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxSize()) {
                if (loading) CircularProgressIndicator(Modifier.align(Alignment.Center))
                if (!error.isNullOrBlank()) Text(text = error, modifier = Modifier.align(Alignment.Center))
            }

            Text(
                text = studyPlan?.title ?: "",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = studyPlan?.userId ?: "",
                style = MaterialTheme.typography.caption,
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ){
                studyPlan?.semesters?.sortedBy { it.order }?.forEach { (semester, courses) ->
                    //val isCollapsed = isSemesterCollapsed[semester] == true
                    val isCollapsed = false
                    stickyHeader {
                        SemesterItemSave(
                            title = "$semester. SEMESTER",
                            isCollapsed = isCollapsed,
                            color = MaterialTheme.colors.primary,
                            isCollapsedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
                            isExpandedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowUp),
                            onClick = {
                                //isSemesterCollapsed[semester] = isCollapsed.not()
                            }
                        )
                    }
                    if (isCollapsed.not()){
                        courses.forEach {
                            item {
                                CourseItemSave(title = it.title)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(73.dp)) }
            }
        }
    }
}
