package com.dtu.trailz.ui.studyplans

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dtu.trailz.R
import com.dtu.trailz.ui.common.DataState
import com.dtu.trailz.ui.common.studyplan.StudyPlanList
import com.dtu.trailz.ui.favorites.StudyPlansUiModel
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
                title = { Text(text = stringResource(R.string.study_plans_toolbar_title)) },
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
                    state = it,
                    onUpdateFavorite = onUpdateFavorite,
                    onStudyPlan = onStudyPlan,
                    onExpandClicked = onExpandClicked
                )
            }
        }
    }
}