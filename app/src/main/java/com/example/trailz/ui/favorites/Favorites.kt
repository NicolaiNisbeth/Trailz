package com.example.trailz.ui.favorites

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import com.example.base.domain.Favorite
import com.example.trailz.ui.studyplanners.StudyPlan
import com.example.trailz.ui.studyplanners.StudyPlans
import kotlinx.coroutines.launch

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
        Column {
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

}