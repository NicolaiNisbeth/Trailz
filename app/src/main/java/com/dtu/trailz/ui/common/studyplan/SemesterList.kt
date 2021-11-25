package com.dtu.trailz.ui.common.studyplan

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dtu.base.domain.Course
import com.dtu.trailz.ui.common.compose.ExpandableContent

@ExperimentalAnimationApi
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
    val list = semesterToCourses.toSortedMap().toList()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Header(
                Modifier.fillMaxWidth(),
                title = title,
                owner = username,
                updatedLast = updatedLast
            )
        }
        items(list.size) { idx ->
            val (semester, courses) = list[idx]
            val isCollapsed = isSemesterCollapsed(semester)
            ExpandableContent(
                isExpanded = isCollapsed.not(),
                fixedContent = { rotationDegree ->
                    SemesterItem(
                        title = "$semester",
                        color = MaterialTheme.colors.primary,
                        isExpandedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowUp),
                        rotationDegree = rotationDegree,
                        onClick = {
                            if (isCollapsed) expandSemester(semester) else collapseSemester(semester)
                        }
                    )
                },
                expandedContent = {
                    Column {
                        courses.forEach {
                            CourseItem(it.title)
                        }
                    }
                }
            )
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
        Text(updatedLast, style = MaterialTheme.typography.overline)
        Text(
            title,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(owner, style = MaterialTheme.typography.caption)
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SemesterItem(
    title: String,
    isExpandedIcon: Painter,
    rotationDegree: Float,
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
            painter = isExpandedIcon,
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .rotate(rotationDegree)
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