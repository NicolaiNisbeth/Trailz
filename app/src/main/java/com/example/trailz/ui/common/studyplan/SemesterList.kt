package com.example.trailz.ui.common.studyplan

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.base.domain.Course
import com.example.trailz.ui.common.compose.EXPAND_ANIMATION_DURATION
import com.example.trailz.ui.common.themeColor

@ExperimentalFoundationApi
@Composable
fun SemesterList(
    modifier: Modifier = Modifier,
    title: String,
    username: String,
    updatedLast: String,
    semesterToCourses: Map<Int, List<Course>>,
    isSemesterCollapsed: (Int) -> Boolean,
    expandSemester: (Int) -> Unit,
    collapseSemester: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Header(
                Modifier.fillMaxWidth(),
                title = title,
                owner = username,
                updatedLast = updatedLast
            )
        }

        semesterToCourses.toSortedMap().forEach { (semester, courses) ->
            val isCollapsed = isSemesterCollapsed(semester)
            item {
                SemesterItem(
                    title = "$semester",
                    isCollapsed = isCollapsed,
                    color = MaterialTheme.colors.primary,
                    isCollapsedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
                    isExpandedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowUp),
                    onClick = {
                        if (isCollapsed) expandSemester(semester) else collapseSemester(semester)
                    }
                )
            }
            if (!isCollapsed) {
                courses.forEach {
                    item { CourseItem(title = it.title) }
                }
            }
        }
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String,
    owner: String,
    updatedLast: String,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(title, style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold))
        Text(owner, style = MaterialTheme.typography.caption)
        Text(updatedLast, style = MaterialTheme.typography.overline)
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SemesterItem(
    title: String,
    isCollapsed: Boolean = true,
    isCollapsedIcon: Painter,
    isExpandedIcon: Painter,
    color: Color,
    onClick: (String) -> Unit
) {
    Box(
        Modifier
            .clickable { onClick(title) }
    ) {
        Spacer(
            modifier = Modifier
                .align(Alignment.Center)
                .height(1.dp)
                .fillMaxWidth()
                .background(color)
        )
        Text(
            text = title,
            color = color,
            modifier = Modifier
                .align(Alignment.Center)
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp)
        )
        Icon(
            contentDescription = null,
            tint = color,
            painter = if (isCollapsed) isCollapsedIcon else isExpandedIcon,
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun CourseItem(
    title: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
        )
    }
}