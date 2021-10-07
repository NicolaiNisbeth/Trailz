package com.example.trailz.ui.mystudyplan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MyStudyPlan(
    viewModel: MyStudyPlanViewModel,
    onProfile: () -> Unit,
    navigateUp: () -> Unit
) {
    MyStudyPlan(
        onProfile = onProfile,
        navigateUp = navigateUp
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MyStudyPlan(
    onProfile: () -> Unit,
    navigateUp: () -> Unit

) {
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

    val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Plan") },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(imageVector = Icons.Default.PermIdentity, contentDescription = null)
                    }
                    IconButton(onClick = { inEdit = !inEdit }){
                        Icon(
                            imageVector = if (inEdit) Icons.Default.Save else Icons.Default.ModeEdit,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Roadmap for Winners",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Nicolai Wolter Hjort Nisbeth",
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = "Updated: $date",
                style = MaterialTheme.typography.overline,
                modifier = Modifier
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ){
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