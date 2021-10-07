package com.example.trailz.ui.studyplanners

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.base.domain.Favorite
import com.example.base.domain.StudyPlan
import com.example.trailz.ui.common.compose.FavoriteButton

@ExperimentalMaterialApi
@Composable
fun StudyPlans(
    viewModel: StudyPlannersViewModel,
    userId: String?,
    onStudyPlan: (String) -> Unit,
    onProfile: () -> Unit
) {
    val studyPlans by viewModel.studyPlans.observeAsState(initial = emptyList())
    val favorites by viewModel.favorite.observeAsState(initial = Favorite())

    StudyPlans(
        studyPlans = studyPlans,
        followedUserIds = favorites.followedUserIds,
        onFavorite = { viewModel.addToFavorite(it, userId) },
        onRemove = { viewModel.removeFromFavorite(it, userId) },
        onStudyPlan = onStudyPlan,
        onProfile = onProfile
    )
}

@ExperimentalMaterialApi
@Composable
fun StudyPlans(
    studyPlans: List<StudyPlan>,
    followedUserIds: List<String>,
    onFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
    onStudyPlan: (String) -> Unit,
    onProfile: () -> Unit
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
        LazyColumn {
            items(studyPlans.size){ index ->
                val studyPlan = studyPlans[index]
                StudyPlan(
                    userId = studyPlan.userId,
                    title = studyPlan.title,
                    checked = studyPlan.userId in followedUserIds,
                    onFavorite = onFavorite,
                    onRemove = onRemove,
                    onStudyPlan = onStudyPlan
                )
            }
        }
    }

}

@ExperimentalMaterialApi
@Composable
fun StudyPlan(
    userId: String,
    title: String,
    checked: Boolean,
    onFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
    onStudyPlan: (String) -> Unit
) {
    Card(
        onClick = { onStudyPlan(userId) }
    ) {
        Column {
            Text(text = title)
            Text(text = userId)
            Row {
                FavoriteButton(isChecked = checked) {
                    if (checked)
                        onRemove(userId)
                    else
                        onFavorite(userId)
                }
            }
        }
    }
}