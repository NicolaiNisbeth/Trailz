package com.example.trailz.ui.studyplan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.base.domain.Course
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.studyplan.common.Header
import com.example.trailz.ui.studyplan.common.SemesterList

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun StudyPlan(
    viewModel: MyStudyPlanViewModel,
    onProfile: () -> Unit,
    navigateUp: () -> Unit
) {
    val semesterToCourses = viewModel.semesterToCourses
    val state by viewModel.state.collectAsState(DataState(isLoading = true))
    StudyPlan(
        state = state,
        semesterToCourses = semesterToCourses,
        isAnyCollapsed = viewModel.isAnyCollapsed(),
        toggleAllCollapsed = viewModel::toggleAllSemesters,
        isSemesterCollapsed = viewModel::isSemesterCollapsed,
        collapseSemester = viewModel::collapseSemester,
        expandSemester = viewModel::expandSemester,
        onProfile = onProfile,
        navigateUp = navigateUp
    )
}

@ExperimentalFoundationApi
@Composable
private fun StudyPlan(
    state: DataState<MyStudyPlanData>,
    semesterToCourses: Map<Int, List<Course>>,
    isAnyCollapsed: Boolean,
    toggleAllCollapsed: (Boolean) -> Unit,
    isSemesterCollapsed: (Int) -> Boolean,
    collapseSemester: (Int) -> Unit,
    expandSemester: (Int) -> Unit,
    onProfile: () -> Unit,
    navigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            Toolbar(
                isAnyCollapsed = isAnyCollapsed,
                navigateUp = navigateUp,
                onProfile = onProfile,
                toggleAllCollapsed = toggleAllCollapsed
            )
        }
    ) {
        state.data?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Header(
                    title = it.title,
                    owner = it.username,
                    updatedLast = "Updated: ${it.updatedLast}"
                )
                SemesterList(
                    semesterToCourses = semesterToCourses,
                    isSemesterCollapsed = isSemesterCollapsed,
                    expandSemester = expandSemester,
                    collapseSemester = collapseSemester
                )
            }
        }
    }
}

@Composable
private fun Toolbar(
    isAnyCollapsed: Boolean,
    navigateUp: () -> Unit,
    onProfile: () -> Unit,
    toggleAllCollapsed: (Boolean) -> Unit
) {
    TopAppBar(
        title = { Text(text = "Study Plan") },
        backgroundColor = MaterialTheme.colors.background,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = onProfile) {
                Icon(Icons.Default.PermIdentity, contentDescription = null)
            }
            IconButton(onClick = { toggleAllCollapsed(isAnyCollapsed) }) {
                Icon(
                    imageVector = if (isAnyCollapsed) Icons.Default.Expand else Icons.Default.Compress,
                    contentDescription = null
                )
            }
        }
    )
}
