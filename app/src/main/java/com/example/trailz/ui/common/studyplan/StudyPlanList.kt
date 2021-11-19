package com.example.trailz.ui.common.studyplan

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.base.domain.Semester
import com.example.trailz.R
import com.example.trailz.ui.common.compose.ExpandableCard
import com.example.trailz.ui.common.compose.FavoriteButton
import com.example.trailz.ui.favorites.StudyPlansUiModel

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun StudyPlanList(
    studyPlans: StudyPlansUiModel,
    onUpdateFavorite: (String, Boolean, Long) -> Unit,
    onStudyPlan: (String) -> Unit,
    onExpandClicked: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        studyPlans.studyPlans.forEach {
            item {
                StudyPlanOverView(
                    id = it.userId,
                    username = it.username,
                    title = it.title,
                    semesters = it.semesters,
                    likes = it.likes,
                    lastUpdated = it.updated,
                    isExpanded = studyPlans.expandedPlans[it.userId] ?: false,
                    isChecked = it.isFavorite,
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
fun StudyPlanOverView(
    modifier: Modifier = Modifier,
    id: String,
    username: String,
    title: String,
    likes: Long,
    lastUpdated: String,
    isChecked: Boolean,
    isExpanded: Boolean,
    semesters: List<Semester>,
    onExpandClicked: () -> Unit,
    onUpdateFavorite: (String, Boolean, Long) -> Unit,
    onStudyPlan: (String) -> Unit
){
    ExpandableCard(
        modifier = modifier.clickable { onStudyPlan(id) },
        isExpanded = isExpanded,
        FixedContent = { arrowRotationDegree ->
            val paddingModifier = Modifier.padding(horizontal = 12.dp)
            Text(text = stringResource(id = R.string.user_likes_args, formatArgs = arrayOf(likes)), style = MaterialTheme.typography.overline, modifier = paddingModifier.padding(top = 16.dp))
            Text(text = title, style = MaterialTheme.typography.button, modifier = paddingModifier)
            Text(text = username, style = MaterialTheme.typography.body2, modifier = paddingModifier)
            Text(text = lastUpdated, style = MaterialTheme.typography.caption, modifier = paddingModifier)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                FavoriteButton(
                    isChecked = isChecked,
                    colorOnChecked = MaterialTheme.colors.primary,
                    colorUnChecked = MaterialTheme.colors.onBackground,
                    onClick = {
                        onUpdateFavorite(id, it, likes)
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
                        Text(
                            text = stringResource(
                                id = R.string.semester_number,
                                formatArgs = arrayOf(semester.order)
                            ),
                            style = MaterialTheme.typography.button
                        )
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