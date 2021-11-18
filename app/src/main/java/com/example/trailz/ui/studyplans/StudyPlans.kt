package com.example.trailz.ui.studyplans

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.studyplan.StudyPlanList
import com.example.trailz.ui.favorites.EmptyScreen
import com.example.trailz.ui.favorites.LoadingScreen
import com.example.trailz.ui.favorites.StudyPlansUiModel

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun StudyPlans(
    viewModel: StudyPlansViewModel,
    onProfile: () -> Unit,
    onStudyPlan: (String) -> Unit
) {

    val state by viewModel.state.collectAsState(DataState(isLoading = true))

    StudyPlans(
        state = state,
        onProfile = onProfile,
        onUpdateFavorite = viewModel::updateChecked,
        onStudyPlan = onStudyPlan,
        onExpandClicked = viewModel::updateExpanded
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun StudyPlans(
    state: DataState<StudyPlansUiModel>,
    onProfile: () -> Unit,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onStudyPlan: (String) -> Unit,
    onExpandClicked: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Study plans") },
                backgroundColor = MaterialTheme.colors.background,
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(imageVector = Icons.Default.PermIdentity, contentDescription = null)
                    }
                }
            )
        }
    ) {
        if (state.isEmpty) {

        }

        if (state.isLoading) {
            LoadingScreen()
        }

        state.data?.let {
            StudyPlanList(
                studyPlans = it,
                onUpdateFavorite = onUpdateFavorite,
                onStudyPlan = onStudyPlan,
                onExpandClicked = onExpandClicked
            )
        }
    }
}