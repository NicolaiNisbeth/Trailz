package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    val semesterCollapsedMemory = remember {
        mutableStateMapOf(
            "1. SEMESTER" to listOf("A", "B", "C"),
            "2. SEMESTER" to listOf("D", "E", "F"),
        )
    }

    val semesterToCourses = remember { mutableStateMapOf(
        "1. SEMESTER" to listOf("A", "B", "C"),
        "2. SEMESTER" to emptyList(),
    )}

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
                            if (expandAll) {
                                semesterToCourses.putAll(semesterCollapsedMemory)
                                semesterCollapsedMemory.clear()
                            } else {
                                for ((sem, courses) in semesterToCourses) {
                                    semesterCollapsedMemory[sem] = courses
                                    semesterToCourses[sem] = emptyList()

                                }
                            }
                            for (semester in semesterToCourses.keys) {
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
                semesterToCourses.forEach { (sem, courses) ->
                    stickyHeader {
                        Box(
                            Modifier.background(MaterialTheme.colors.background)
                                .clickable {
                                    if (isSemesterCollapsed[sem] == true){
                                        semesterCollapsedMemory.remove(sem)?.let {
                                            semesterToCourses[sem] = it
                                        }
                                        isSemesterCollapsed[sem] = false
                                    } else {
                                        semesterToCourses[sem]?.let {
                                            semesterCollapsedMemory[sem] = it
                                            semesterToCourses[sem] = emptyList()
                                        }
                                        isSemesterCollapsed[sem] = true
                                    }
                                }
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .height(1.dp)
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.primary)
                            )
                            Text(
                                text = sem,
                                color = MaterialTheme.colors.primary,
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
                                tint = MaterialTheme.colors.primary,
                                imageVector = if (isSemesterCollapsed[sem] == true)
                                    Icons.Default.KeyboardArrowDown
                                else
                                    Icons.Default.KeyboardArrowUp
                            )
                        }
                    }
                    courses.forEach {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.caption,
                                )
                                Text(
                                    text = "Remove",
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.caption,
                                    modifier = Modifier.clickable { semesterToCourses[sem] = courses.minus(it) }
                                )

                            }
                        }
                    }
                    if ((isSemesterCollapsed[sem] == true).not()){
                        item {
                            Text(
                                text = "Add",
                                color = MaterialTheme.colors.secondaryVariant,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.clickable {
                                    semesterToCourses[sem] = courses.plus("new")
                                }
                            )
                        }
                    }
                }
                item {
                    Box(
                        Modifier.background(MaterialTheme.colors.background)
                            .clickable {
                                isSemesterCollapsed["3. SEMESTER"] = false
                                semesterToCourses["3. SEMESTER"] = emptyList()
                            }
                    ) {
                        Spacer(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.secondaryVariant)
                        )
                        Text(
                            text = "X. SEMESTER",
                            color = MaterialTheme.colors.secondaryVariant,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(MaterialTheme.colors.background)
                                .padding(horizontal = 16.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondaryVariant,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .background(MaterialTheme.colors.background)
                        )
                    }
                }
                item {
                    Text(
                        text = "Last modified: ${SimpleDateFormat("yyyy/MM/dd").format(Date())}",
                        style = MaterialTheme.typography.overline,
                        modifier = Modifier
                    )
                }
                item { Spacer(modifier = Modifier.height(73.dp)) }
            }

        }
    }
}

