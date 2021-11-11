package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.base.domain.Course
import com.example.trailz.ui.common.Event
import com.example.trailz.ui.mystudyplan.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun StudyPlanner(
    viewModel: MyStudyPlanViewModel,
    onProfile: () -> Unit,
    navigateUp: () -> Unit
) {
    val semesterToCourses = viewModel.semesterToCourses
    val header by viewModel.header
    StudyPlanner(
        header = header,
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
fun StudyPlanner(
    header: Pair<String, String>,
    semesterToCourses: Map<Int, List<Course>>,
    isAnyCollapsed: Boolean,
    toggleAllCollapsed: (Boolean) -> Unit,
    isSemesterCollapsed: (Int) -> Boolean,
    collapseSemester: (Int) -> Unit,
    expandSemester: (Int) -> Unit,
    onProfile: () -> Unit,
    navigateUp: () -> Unit
) {
    val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Header(
                title = header.second,
                owner = header.first,
                updatedLast = "Updated: $date"
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

@Composable
fun Toolbar(
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
