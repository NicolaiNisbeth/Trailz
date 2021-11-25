package com.dtu.trailz.ui.common.studyplan

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dtu.base.domain.Semester
import com.dtu.trailz.R
import com.dtu.trailz.ui.common.compose.ExpandableCard
import com.dtu.trailz.ui.common.compose.FavoriteButton
import com.dtu.trailz.ui.favorites.StudyPlansUiModel

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun StudyPlanList(
    state: StudyPlansUiModel,
    onUpdateFavorite: (String, Boolean, Long) -> Unit,
    onStudyPlan: (String) -> Unit,
    onExpandClicked: (String) -> Unit
) {
    val studyPlans = state.studyPlans.sortedBy { it.title }.toList()
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(studyPlans.size) { idx ->
            val studyPlan = studyPlans[idx]
            StudyPlanOverView(
                id = studyPlan.userId,
                username = studyPlan.username,
                title = studyPlan.title,
                semesters = studyPlan.semesters,
                likes = studyPlan.likes,
                lastUpdated = studyPlan.updated,
                isExpanded = state.expandedPlans[studyPlan.userId] ?: false,
                isChecked = studyPlan.isFavorite,
                onExpandClicked = { onExpandClicked(studyPlan.userId) },
                onUpdateFavorite = onUpdateFavorite,
                onStudyPlan = onStudyPlan
            )
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
) {
    ExpandableCard(
        modifier = modifier.clickable { onStudyPlan(id) },
        isExpanded = isExpanded,
        fixedContent = { arrowRotationDegree ->
            Metadata(
                id = id,
                username = username,
                title = title,
                likes = likes,
                lastUpdated = lastUpdated,
                isChecked = isChecked,
                onExpandClicked = onExpandClicked,
                onUpdateFavorite = onUpdateFavorite,
                anySemesters = semesters.any { it.courses.any { it.title.isNotBlank() } },
                arrowRotationDegree = arrowRotationDegree
            )
        },
        expandedContent = {
            StudyPlan(semesters)
        }
    )
}

@Composable
private fun Metadata(
    id: String,
    username: String,
    title: String,
    likes: Long,
    lastUpdated: String,
    isChecked: Boolean,
    onExpandClicked: () -> Unit,
    onUpdateFavorite: (String, Boolean, Long) -> Unit,
    anySemesters: Boolean,
    arrowRotationDegree: Float
) {
    val paddingModifier = Modifier.padding(horizontal = 12.dp)
    val typography = MaterialTheme.typography
    Text(
        text = stringResource(
            id = R.string.user_likes_args,
            formatArgs = arrayOf(likes)
        ).toUpperCase(),
        style = typography.overline,
        modifier = paddingModifier.padding(top = 16.dp, bottom = 4.dp)
    )
    Text(
        text = title,
        style = typography.h6.copy(fontWeight = FontWeight.Bold),
        modifier = paddingModifier.padding(bottom = 4.dp)
    )
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Row(paddingModifier) {
            Text(
                text = stringResource(
                    id = R.string.study_plan_author_date,
                    formatArgs = arrayOf(
                        username.capitalize(),
                        lastUpdated
                    )
                ),
                style = typography.body2
            )
        }
    }
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FavoriteButton(
            isChecked = isChecked,
            colorOnChecked = MaterialTheme.colors.primary,
            colorUnChecked = MaterialTheme.colors.onBackground,
            onClick = { onUpdateFavorite(id, it, likes) }
        )
        if (anySemesters) {
            IconButton(
                modifier = Modifier.testTag("expandButton"),
                onClick = onExpandClicked,
                content = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expandable Arrow",
                        modifier = Modifier.rotate(arrowRotationDegree),
                    )
                }
            )
        }
    }
}

@Composable
private fun StudyPlan(
    semesters: List<Semester>
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            .testTag("semesters")
    ) {
        semesters.forEachIndexed { index, semester ->
            if (semester.courses.isNotEmpty()) {
                Text(
                    style = MaterialTheme.typography.button,
                    text = stringResource(
                        id = R.string.semester_number,
                        formatArgs = arrayOf(semester.order)
                    )
                )
                for (course in semester.courses) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.overline,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            if (index != semesters.lastIndex) {
                Spacer(Modifier.heightIn(4.dp))
            }
        }
    }
}