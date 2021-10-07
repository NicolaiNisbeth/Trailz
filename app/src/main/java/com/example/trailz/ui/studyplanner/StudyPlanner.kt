package com.example.trailz.ui.studyplanner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalComposeUiApi
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
            "1. SEMESTER" to listOf("A", "BBBBBBB", "C"),
            "2. SEMESTER" to emptyList(),
        )
    }

    var inEdit by remember {
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
                IconButton(
                    onClick = {inEdit = !inEdit},
                    modifier = Modifier.align(Alignment.TopEnd)
                ){
                    Icon(
                        imageVector = if (inEdit) Icons.Default.Save else Icons.Default.ModeEdit,
                        contentDescription = null
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                if (inEdit){
                    item {
                        SemesterItemSave(
                            title = "X. SEMESTER",
                            isCollapsedIcon = rememberVectorPainter(image = Icons.Default.Add),
                            isExpandedIcon = rememberVectorPainter(image = Icons.Default.Add),
                            color = MaterialTheme.colors.secondaryVariant,
                            onClick = {
                                val key = "${semesterToCourses.size + 1}. SEMESTER"
                                isSemesterCollapsed[key] = false
                                semesterToCourses[key] = emptyList()
                            }
                        )
                    }
                }
                semesterToCourses.toSortedMap().forEach { (semester, courses) ->
                    val isCollapsed = isSemesterCollapsed[semester] == true
                    if (inEdit){
                        stickyHeader {
                            SemesterItemEdit(
                                title = semester,
                                isCollapsed = isCollapsed,
                                color = MaterialTheme.colors.primary,
                                isCollapsedIcon = rememberVectorPainter(image = Icons.Default.Clear),
                                isExpandedIcon = rememberVectorPainter(image = Icons.Default.Clear),
                                onClick = { semesterToCourses.remove(it) }
                            )
                        }

                        courses.forEachIndexed { index, course ->
                            item {
                                CourseItemEdit(
                                    title = course,
                                    onRemove = {
                                        semesterToCourses[semester] = courses.minus(it)
                                   },
                                    onTitleChange = { newCourse -> semesterToCourses[semester]
                                        ?.toMutableList()
                                        ?.let { newCourseList ->
                                            newCourseList[index] = newCourse
                                            semesterToCourses[semester] = newCourseList
                                        }
                                    }
                                )
                            }
                        }
                        item {
                            Text(
                                text = "Add",
                                color = MaterialTheme.colors.secondaryVariant,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.clickable {
                                    semesterToCourses[semester] = courses.plus("?")
                                }
                            )
                        }
                    } else {
                        stickyHeader {
                            SemesterItemSave(
                                title = semester,
                                isCollapsed = isCollapsed,
                                color = MaterialTheme.colors.primary,
                                isCollapsedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
                                isExpandedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowUp),
                                onClick = { isSemesterCollapsed[semester] = isCollapsed.not() }
                            )
                        }
                        if (isCollapsed.not()){
                            courses.forEach {
                                item {
                                    CourseItemSave(title = it)
                                }
                            }
                        }
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
fun SemesterItemSave(
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
fun SemesterItemEdit(
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

@ExperimentalComposeUiApi
@Composable
fun CourseItemEdit(
    title: String,
    onRemove: (String) -> Unit,
    onTitleChange: (String) -> Unit
){
    val focusManager = LocalFocusManager.current
    var newTitle by remember { mutableStateOf(title) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = newTitle,
            onValueChange = { newTitle = it},
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {
                onTitleChange(newTitle)
                focusManager.clearFocus()
            }),
        )

        Text(
            text = "Remove",
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.clickable { onRemove(newTitle) }
        )
    }
}

@Composable
fun CourseItemSave(
    title: String,
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
    }
}

