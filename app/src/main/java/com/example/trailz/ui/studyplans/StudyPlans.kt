package com.example.trailz.ui.studyplans

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.studyplan.StudyPlanList
import com.example.trailz.ui.favorites.LoadingScreen
import com.example.trailz.ui.favorites.StudyPlansUiModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

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
        onUpdateFavorite = viewModel::updateFavorite,
        onStudyPlan = onStudyPlan,
        onExpandClicked = viewModel::updateExpanded,
        refreshStudyPlans = viewModel::refreshStudyPlans
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun StudyPlans(
    state: DataState<StudyPlansUiModel>,
    onProfile: () -> Unit,
    onUpdateFavorite: (String, Boolean, Long) -> Unit,
    onStudyPlan: (String) -> Unit,
    onExpandClicked: (String) -> Unit,
    refreshStudyPlans: (Boolean) -> Unit
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
        state.data?.let {
            SwipeRefresh(
                modifier = Modifier.fillMaxSize(),
                state = SwipeRefreshState(state.isLoading),
                onRefresh = { refreshStudyPlans(true) }
            ) {
                StudyPlanList(
                    studyPlans = it,
                    onUpdateFavorite = onUpdateFavorite,
                    onStudyPlan = onStudyPlan,
                    onExpandClicked = onExpandClicked
                )
            }
        }
    }
}