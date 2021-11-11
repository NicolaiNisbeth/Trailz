package com.example.trailz.ui.favorites

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.base.domain.Semester
import com.example.trailz.ui.common.compose.ExpandableCard
import com.example.trailz.ui.common.compose.FavoriteButton
import com.example.trailz.ui.studyplanners.DataState
import kotlinx.coroutines.flow.MutableStateFlow

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
    state: DataState<FavoritesData>,
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
            StudyPlansScreen(
                studyPlans = it,
                onUpdateFavorite = onUpdateFavorite,
                onStudyPlan = onStudyPlan,
                onExpandClicked = onExpandClicked
            )
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun StudyPlansScreen(
    studyPlans: FavoritesData,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onStudyPlan: (String) -> Unit,
    onExpandClicked: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        studyPlans.studyPlans.forEach {
            item {
                StudyPlan(
                    id = it.userId,
                    username = it.username,
                    title = it.title,
                    semesters = it.semesters,
                    likes = "${it.likes} likes",
                    lastUpdated = it.updated,
                    isExpanded = studyPlans.expandedPlans[it.userId] ?: false,
                    isChecked = true,
                    onExpandClicked = { onExpandClicked(it.userId) },
                    onUpdateFavorite = onUpdateFavorite,
                    onStudyPlan = onStudyPlan
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun StudyPlan(
    modifier: Modifier = Modifier,
    id: String,
    username: String,
    title: String,
    likes: String,
    lastUpdated: String,
    isChecked: Boolean,
    isExpanded: Boolean,
    semesters: List<Semester>,
    onExpandClicked: () -> Unit,
    onUpdateFavorite: (String, Boolean) -> Unit,
    onStudyPlan: (String) -> Unit
){
    ExpandableCard(
        modifier = modifier.clickable { onStudyPlan(id) },
        isExpanded = isExpanded,
        fixedContent = { arrowRotationDegree ->
            val paddingModifier = Modifier.padding(horizontal = 12.dp)
            Text(text = likes, style = MaterialTheme.typography.overline, modifier = paddingModifier.padding(top = 16.dp))
            Text(text = title, style = MaterialTheme.typography.button, modifier = paddingModifier)
            Text(text = username, style = MaterialTheme.typography.body2, modifier = paddingModifier)
            Text(text = lastUpdated, style = MaterialTheme.typography.caption, modifier = paddingModifier)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                FavoriteButton(
                    isChecked = isChecked,
                    colorOnChecked = MaterialTheme.colors.primary,
                    colorUnChecked = MaterialTheme.colors.onBackground,
                    onClick = {
                        onUpdateFavorite(id, it)
                    }
                )
                if (semesters.any { it.courses.any { it.title.isNotBlank() } }){
                    IconButton(
                        onClick = onExpandClicked,
                        content = {
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "Expandable Arrow",
                                modifier = Modifier.rotate(arrowRotationDegree),
                            )
                        }
                    )
                }
            }
        },
        expandableContent = {
            Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                semesters.forEachIndexed { index, semester ->
                    if (semester.courses.isNotEmpty()){
                        Text(text = "${semester.order}. Semester", style = MaterialTheme.typography.button)
                        for (course in semester.courses) {
                            Text(text = course.title, style = MaterialTheme.typography.overline)
                        }
                    }
                    if (index != semesters.lastIndex){
                        Spacer(Modifier.heightIn(4.dp))
                    }
                }
            }
        }
    )
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
