package com.example.trailz.ui.favorites

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.studyplan.StudyPlanList

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Favorites(
    viewModel: FavoritesViewModel,
    userId: String,
    onStudyPlan: (String) -> Unit,
    onProfile: () -> Unit,
    onFindFavorite: () -> Unit
) {

    val state by viewModel.state.collectAsState(DataState(isLoading = true))

    Favorites(
        state = state,
        onStudyPlan = onStudyPlan,
        onProfile = onProfile,
        onFindFavorite = onFindFavorite,
        onUpdateFavorite = { favoriteId, isChecked ->
            viewModel.updateFavorite(favoriteId, userId, isChecked)
        },
        onExpandClicked = { viewModel.updateExpanded(it) }
    )
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Favorites(
    state: DataState<StudyPlansUiModel>,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onStudyPlan: (String) -> Unit,
    onProfile: () -> Unit,
    onFindFavorite: () -> Unit,
    onExpandClicked: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Favoritter") },
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
            EmptyScreen(onFindFavorite)
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

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyScreen(onFindFavorite: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null)
        Text(text = "You have no favorites yet!")
        Button(onClick = onFindFavorite) {
            Text(text = "Find your favorite")
        }
    }
}
