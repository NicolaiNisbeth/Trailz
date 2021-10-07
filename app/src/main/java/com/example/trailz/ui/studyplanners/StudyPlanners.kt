package com.example.trailz.ui.studyplanners

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.base.domain.Favorite
import com.example.base.domain.StudyPlan
import com.example.trailz.R
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
            item { Spacer(modifier = Modifier.height(73.dp)) }
        }
    }

}

@ExperimentalMaterialApi
@Composable
fun StudyPlan(
    modifier: Modifier = Modifier,
    userId: String,
    title: String,
    checked: Boolean,
    onFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
    onStudyPlan: (String) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
        onClick = { onStudyPlan(userId) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val imageModifier = Modifier
                .heightIn(max = 180.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 6.dp).align(Alignment.TopStart)
                )

                Text(
                    text = userId,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(bottom = 4.dp).align(Alignment.BottomStart)
                )

                FavoriteButton(isChecked = checked, modifier = Modifier.align(Alignment.CenterEnd)) {
                    if (checked) onRemove(userId)
                    else onFavorite(userId)
                }
            }

            Spacer(Modifier.height(16.dp))

        }
    }
}