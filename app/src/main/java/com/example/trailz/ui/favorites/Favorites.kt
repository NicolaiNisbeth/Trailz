package com.example.trailz.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.base.domain.StudyPlan
import com.example.trailz.ui.studyplanners.DataState
import com.example.trailz.ui.studyplanners.StudyPlan

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
        }
    )
}

@ExperimentalMaterialApi
@Composable
fun Favorites(
    state: DataState<List<StudyPlan>>,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onStudyPlan: (String) -> Unit,
    onProfile: () -> Unit,
    onFindFavorite: () -> Unit
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
            StudyPlansScreen(
                studyPlans = it,
                onUpdateFavorite = onUpdateFavorite,
                onStudyPlan = onStudyPlan
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun StudyPlansScreen(
    studyPlans: List<StudyPlan>,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onStudyPlan: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (studyPlan in studyPlans) {
            item {
                StudyPlan(
                    userId = studyPlan.userId,
                    title = studyPlan.title,
                    checked = true,
                    onUpdateFavorite = onUpdateFavorite,
                    onStudyPlan = onStudyPlan
                )
            }
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
