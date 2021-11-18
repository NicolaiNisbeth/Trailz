package com.example.trailz.ui.studyplan.common

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.base.domain.Course

@Composable
fun Header(title: String, owner: String, updatedLast: String) {
    Text(title, style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold))
    Text("Created by $owner", style = MaterialTheme.typography.caption)
    Text(updatedLast, style = MaterialTheme.typography.overline)
}

@ExperimentalFoundationApi
@Composable
fun SemesterList(
    semesterToCourses: Map<Int, List<Course>>,
    isSemesterCollapsed: (Int) -> Boolean,
    expandSemester: (Int) -> Unit,
    collapseSemester: (Int) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        semesterToCourses.toSortedMap().forEach { (semester, courses) ->
            val isCollapsed = isSemesterCollapsed(semester)
            stickyHeader {
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
            .background(MaterialTheme.colors.background)
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
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 16.dp)
        )
        Icon(
            contentDescription = null,
            tint = color,
            painter = if (isCollapsed) isCollapsedIcon else isExpandedIcon,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(MaterialTheme.colors.background)
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
