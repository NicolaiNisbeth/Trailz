package com.example.trailz.ui.mystudyplan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.base.domain.Course
import com.example.base.domain.StudyPlan
import com.example.trailz.ui.common.compose.InputFieldDialog
import com.example.trailz.ui.common.compose.InputFieldFocus
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

    val studyPlan by viewModel.unsavedStudyPlan.observeAsState(StudyPlan())
    val semesterToCourses = viewModel.semesterToCourses
    val inEditMode by viewModel.inEditMode

    MyStudyPlan(
        studyPlan = studyPlan,
        semesterToCourses = semesterToCourses,
        inEditMode = inEditMode,
        toggleAllCollapsed = viewModel::toggleAllSemesters,
        isAnyCollapsed = viewModel::isAnyCollapsed,
        changeEditMode = viewModel::changeEditMode,
        isSemesterCollapsed = viewModel::isSemesterCollapsed,
        collapseSemester = viewModel::collapseSemester,
        expandSemester = viewModel::expandSemester,
        addSemester = viewModel::addSemester,
        removeSemester = viewModel::removeSemester,
        editSemester = viewModel::editSemester,
        addCourse = viewModel::addCourse,
        removeCourse = viewModel::removeCourse,
        replaceCourseAt = viewModel::replaceCourseAt,
        onProfile = onProfile,
        navigateUp = navigateUp
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MyStudyPlan(
    studyPlan: StudyPlan,
    semesterToCourses: Map<Int, List<Course>>,
    inEditMode: Boolean,
    toggleAllCollapsed: (Boolean) -> Unit,
    isAnyCollapsed: () -> Boolean,
    changeEditMode: (Boolean) -> Unit,
    isSemesterCollapsed: (Int) -> Boolean,
    collapseSemester: (Int) -> Unit,
    expandSemester: (Int) -> Unit,
    addSemester: () -> Unit,
    removeSemester: (Int) -> Unit,
    editSemester: (Int, String) -> Unit,
    addCourse: (Course, Int) -> Unit,
    replaceCourseAt: (Int, Int, Course) -> Unit,
    removeCourse: (Course, Int) -> Unit,
    onProfile: () -> Unit,
    navigateUp: () -> Unit

) {
    val focusManager = LocalFocusManager.current
    val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

    var openDialog by remember { mutableStateOf(false) }
    var newTitleSemester by remember { mutableStateOf(-1) }

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        addCourse(Course(it), newTitleSemester)
        openDialog = false
    }

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
                    if (inEditMode){
                        IconButton(onClick = addSemester ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    } else {
                        val collapsed = isAnyCollapsed()
                        val icon = if (collapsed) Icons.Default.Expand else Icons.Default.Compress
                        IconButton(onClick = { toggleAllCollapsed(collapsed) }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    }
                    IconButton(onClick = onProfile) {
                        Icon(imageVector = Icons.Default.PermIdentity, contentDescription = null)
                    }
                    IconButton(onClick = { changeEditMode(!inEditMode) }){
                        Icon(
                            imageVector = if (inEditMode) Icons.Default.Save else Icons.Default.ModeEdit,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
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
                    val isCollapsed = isSemesterCollapsed(semester)
                    if (inEditMode){
                        stickyHeader {
                            SemesterItemEdit(
                                title = "$semester",
                                isCollapsed = isCollapsed,
                                color = MaterialTheme.colors.primary,
                                isCollapsedIcon = rememberVectorPainter(image = Icons.Default.Clear),
                                isExpandedIcon = rememberVectorPainter(image = Icons.Default.Clear),
                                onClick = { removeSemester(semester) },
                                onTitleChange = { editSemester(semester, it) }
                            )
                        }

                        courses.forEachIndexed { index, course ->
                            item {
                                CourseItemEdit(
                                    title = course.title,
                                    onRemove = { removeCourse(course, semester) },
                                    onTitleChange = { title -> replaceCourseAt(index, semester, Course(title)) }
                                )
                            }
                        }
                        item {
                            Text(
                                text = "Add",
                                color = MaterialTheme.colors.secondaryVariant,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.clickable {
                                    newTitleSemester = semester
                                    openDialog = true
                                }
                            )
                        }
                    } else {
                        stickyHeader {
                            SemesterItemSave(
                                title = "$semester",
                                isCollapsed = isCollapsed,
                                color = MaterialTheme.colors.primary,
                                isCollapsedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
                                isExpandedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowUp),
                                onClick = { if (isSemesterCollapsed(semester)) expandSemester(semester) else collapseSemester(semester) }
                            )
                        }
                        if (isCollapsed.not()){
                            courses.forEach {
                                item {
                                    CourseItemSave(title = it.title)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(73.dp)) }
            }

            if (openDialog) {
                InputFieldDialog(
                    title = "What course is this?",
                    confirmTitle = "Confirm",
                    dismissTitle = "Dismiss",
                    onConfirm = submitNameChange,
                    onDismiss = { openDialog = false }
                ){ value, onValueChange ->
                    InputFieldFocus { focusModifier ->
                        TextField(
                            modifier = focusModifier,
                            value = value,
                            onValueChange = onValueChange,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Password
                            ),
                            keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
                        )
                    }
                }
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

@ExperimentalComposeUiApi
@Composable
fun SemesterItemEdit(
    modifier: Modifier = Modifier,
    title: String,
    isCollapsed: Boolean = true,
    isCollapsedIcon: Painter,
    isExpandedIcon: Painter,
    color: Color,
    onClick: (String) -> Unit,
    onTitleChange: (String) -> Unit
){
    var openDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        onTitleChange(it)
        openDialog = false
    }

    Box(
        modifier
            .background(MaterialTheme.colors.background)
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
            color = MaterialTheme.colors.secondaryVariant,
            modifier = Modifier
                .align(Alignment.Center)
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 16.dp)
                .clickable { openDialog = true }
        )
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(MaterialTheme.colors.background)
                .clickable { onClick(title) },
            contentDescription = null,
            tint = color,
            painter = if (isCollapsed) isCollapsedIcon else isExpandedIcon
        )

        if (openDialog) {
            InputFieldDialog(
                title = "What semester is it?",
                confirmTitle = "Confirm",
                dismissTitle = "Dismiss",
                onConfirm = submitNameChange,
                onDismiss = { openDialog = false }
            ){ value, onValueChange ->
                InputFieldFocus { focusModifier ->
                    TextField(
                        modifier = focusModifier,
                        value = value,
                        onValueChange = onValueChange,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
                    )
                }
            }
        }
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
    var openDialog by remember { mutableStateOf(false) }

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        onTitleChange(it)
        openDialog = false
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.clickable { openDialog = true }
        )
        Text(
            text = "Remove",
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.clickable { onRemove(title) }
        )
    }

    if (openDialog) {
        InputFieldDialog(
            title = "Course name",
            confirmTitle = "Confirm",
            dismissTitle = "Dismiss",
            onConfirm = submitNameChange,
            onDismiss = { openDialog = false }
        ){ value, onValueChange ->
            InputFieldFocus { focusModifier ->
                TextField(
                    modifier = focusModifier,
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
                )
            }
        }
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