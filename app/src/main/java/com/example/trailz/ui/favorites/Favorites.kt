package com.example.trailz.ui.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.base.domain.Favorite
import com.example.trailz.ui.studyplanners.StudyPlan
import com.example.trailz.ui.studyplanners.StudyPlans

@ExperimentalMaterialApi
@Composable
fun Favorites(
    viewModel: FavoritesViewModel,
    userId: String?,
    onStudyPlan: (String) -> Unit,
    onProfile: () -> Unit
) {

    val favorites by viewModel.favorite.observeAsState(initial = Favorite())

    Favorites(
        favorite = favorites,
        onFavorite = { viewModel.addToFavorite(it, userId) },
        onRemove = { viewModel.removeFromFavorite(it, userId) },
        onStudyPlan = onStudyPlan,
        onProfile = onProfile
    )
}

@ExperimentalMaterialApi
@Composable
fun Favorites(
    favorite: Favorite,
    onFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
    onStudyPlan: (String) -> Unit,
    onProfile: () -> Unit
) {

    Column {
        Button(onClick = onProfile) {
            Text(text = "Profile")
        }
        favorite.followedUserIds.forEach {
            StudyPlan(
                userId = it,
                title = it,
                checked = true,
                onFavorite = onFavorite,
                onRemove = onRemove,
                onStudyPlan = onStudyPlan
            )
        }
    }
}