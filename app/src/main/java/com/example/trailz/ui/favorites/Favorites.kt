package com.example.trailz.ui.favorites

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onProfile: () -> Unit,
    onFindFavorite: () -> Unit
) {

    val favorites by viewModel.favorite.observeAsState(initial = Favorite())

    val isLoading by viewModel.isLoading.observeAsState(initial = true)

    Favorites(
        favorite = favorites,
        isLoading = isLoading,
        onFavorite = { viewModel.addToFavorite(it, userId) },
        onRemove = { viewModel.removeFromFavorite(it, userId) },
        onStudyPlan = onStudyPlan,
        onProfile = onProfile,
        onFindFavorite = onFindFavorite
    )
}

@ExperimentalMaterialApi
@Composable
fun Favorites(
    favorite: Favorite,
    isLoading: Boolean,
    onFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
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
            if (!isLoading && favorite.followedUserIds.isEmpty()){
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

        }
    }

}