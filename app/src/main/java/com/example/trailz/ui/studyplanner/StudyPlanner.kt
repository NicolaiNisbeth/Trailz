package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalFoundationApi
@Composable
fun StudyPlanner(
    openMarketplace: () -> Unit
){
    val isSemesterCollapsed = remember {
        mutableStateMapOf(
            "1. SEMESTER" to false,
            "2. SEMESTER" to true
        )
    }

    val semesterToCourses = remember {
        mutableStateMapOf(
            "1. SEMESTER" to listOf("A", "B", "C"),
            "2. SEMESTER" to emptyList(),
        )
    }

    var expandAll by remember {
        mutableStateOf(false)
    }

    Scaffold {
        Column(
            Modifier.padding(it)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.15f)
            ) {
                Text(
                    text = "Roadmap for Winners",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = "Nicolai Wolter Hjort Nisbeth",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                Text(
                    text = if (expandAll) "Expand" else "Collapse",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clickable {
                            semesterToCourses.keys.forEach { semester ->
                                isSemesterCollapsed[semester] = !expandAll
                            }
                            expandAll = !expandAll
                        }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                semesterToCourses.toSortedMap().forEach { (sem, courses) ->
                    val isCollapsed = isSemesterCollapsed[sem] == true
                    stickyHeader {
                        SemesterItem(
                            title = sem,
                            isCollapsed = isCollapsed,
                            color = MaterialTheme.colors.primary,
                            isCollapsedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
                            isExpandedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowUp),
                            onClick = { isSemesterCollapsed[sem] = isCollapsed.not() }
                        )
                    }
                    if (isCollapsed.not()){
                        courses.forEach {
                            item {
                                CourseItem(
                                    title = it,
                                    onClick = { semesterToCourses[sem] = courses.minus(it) }
                                )
                            }
                        }
                        item {
                            Text(
                                text = "Add",
                                color = MaterialTheme.colors.secondaryVariant,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.clickable {
                                    semesterToCourses[sem] = courses.plus("?")
                                }
                            )
                        }

                    }
                }
                if (semesterToCourses.size < 9){
                    item {
                        SemesterItem(
                            title = "X. SEMESTER",
                            isCollapsedIcon = rememberVectorPainter(image = Icons.Default.Add),
                            isExpandedIcon = rememberVectorPainter(image = Icons.Default.Add),
                            color = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
                            onClick = {
                                val key = "${semesterToCourses.size + 1}. SEMESTER"
                                isSemesterCollapsed[key] = false
                                semesterToCourses[key] = emptyList()
                            }
                        )
                    }
                }
                item {
                    val pattern = "yyyy/MM/dd"
                    val date = SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
                    Text(
                        text = "Last modified: $date",
                        style = MaterialTheme.typography.overline,
                        modifier = Modifier
                    )
                }
                item { Spacer(modifier = Modifier.height(73.dp)) }
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
){
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
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(MaterialTheme.colors.background),
            contentDescription = null,
            tint = color,
            painter = if (isCollapsed) isCollapsedIcon else isExpandedIcon
        )
    }
}

@Composable
fun CourseItem(
    title: String,
    onClick: (String) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
        )
        Text(
            text = "Remove",
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.clickable { onClick(title) }
        )

    }
}

