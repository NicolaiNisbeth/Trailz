package com.example.trailz.ui.studyplan.mystudyplan

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.base.domain.Course
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.compose.InputFieldDialog
import com.example.trailz.ui.common.compose.InputFieldFocus
import com.example.trailz.ui.common.compose.TextButtonV2
import com.example.trailz.ui.common.studyplan.SemesterList
import com.example.trailz.ui.studyplan.MyStudyPlanData
import com.example.trailz.ui.studyplan.MyStudyPlanViewModel
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MyStudyPlan(
    viewModel: MyStudyPlanViewModel,
    onProfile: () -> Unit,
    navigateUp: () -> Unit
) {

    val semesterToCourses = viewModel.semesterToCourses
    val state by viewModel.state.collectAsState(DataState(isLoading = true))

    MyStudyPlan(
        state = state,
        semesterToCourses = semesterToCourses,
        onProfile = onProfile,
        navigateUp = navigateUp,
        isAnyCollapsed = viewModel::isAnyCollapsed,
        toggleAllCollapsed = viewModel::toggleAllSemesters,
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
        saveStudyPlan = viewModel::saveStudyPlan,
        editStudyPlanTitle = viewModel::editStudyPlanTitle
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
private fun MyStudyPlan(
    state: DataState<MyStudyPlanData>,
    semesterToCourses: Map<Int, List<Course>>,
    isAnyCollapsed: () -> Boolean,
    toggleAllCollapsed: (Boolean) -> Unit,
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
    saveStudyPlan: () -> Unit,
    onProfile: () -> Unit,
    editStudyPlanTitle: (String) -> Unit,
    navigateUp: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    state.data?.isUpdated?.contentIfNotHandled()?.let {
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = if (it) "Saved" else "Failed to save!"
            )
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "My Plan") },
                backgroundColor = MaterialTheme.colors.background,
                actions = {
                    state.data?.let {
                        if (it.inEditMode) {
                            EditActionBar(
                                onProfile = onProfile,
                                addSemester = addSemester,
                                saveStudyPlan = saveStudyPlan,
                                changeEditMode = changeEditMode
                            )
                        } else {
                            ActionBar(
                                isAnyCollapsed = isAnyCollapsed(),
                                toggleAllCollapsed = toggleAllCollapsed,
                                onProfile = onProfile,
                                changeEditMode = changeEditMode
                            )
                        }
                    }
                }
            )
        }
    ) {
        state.data?.let {
            val elevation = if (MaterialTheme.colors.isLight) 2.dp else 0.dp
            if (it.inEditMode) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    elevation = elevation
                ) {
                    SemesterListEdit(
                        title = it.title,
                        username = "Created by ${it.username}",
                        updated = "Updated: ${it.updatedLast}",
                        editStudyPlanTitle = editStudyPlanTitle,
                        semesterToCourses = semesterToCourses,
                        isSemesterCollapsed = isSemesterCollapsed,
                        removeCourse = removeCourse,
                        removeSemester = removeSemester,
                        editSemester = editSemester,
                        replaceCourseAt = replaceCourseAt,
                        addCourse = addCourse
                    )
                }
            } else {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    elevation = elevation
                ) {
                    SemesterList(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                        title = it.title,
                        username = "Created by ${it.username}",
                        updatedLast = "Updated: ${it.updatedLast}",
                        semesterToCourses = semesterToCourses,
                        isSemesterCollapsed = isSemesterCollapsed,
                        expandSemester = expandSemester,
                        collapseSemester = collapseSemester
                    )
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
private fun SemesterListEdit(
    title: String,
    username: String,
    updated: String,
    editStudyPlanTitle: (String) -> Unit,
    semesterToCourses: Map<Int, List<Course>>,
    isSemesterCollapsed: (Int) -> Boolean,
    removeCourse: (Course, Int) -> Unit,
    removeSemester: (Int) -> Unit,
    editSemester: (Int, String) -> Unit,
    replaceCourseAt: (Int, Int, Course) -> Unit,
    addCourse: (Course, Int) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var newTitleSemester by rememberSaveable { mutableStateOf(-1) }

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        addCourse(Course(it), newTitleSemester)
        openDialog = false
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            HeaderEdit(
                Modifier.fillMaxWidth(),
                title = title,
                owner = username,
                updatedLast = updated,
                onTitleChange = editStudyPlanTitle
            )
        }

        semesterToCourses.toSortedMap().forEach { (semesterNum, courses) ->
            val isCollapsed = isSemesterCollapsed(semesterNum)
            item {
                SemesterItemEdit(
                    title = "$semesterNum",
                    isCollapsed = isCollapsed,
                    color = MaterialTheme.colors.primary,
                    isCollapsedIcon = rememberVectorPainter(image = Icons.Default.Clear),
                    isExpandedIcon = rememberVectorPainter(image = Icons.Default.Clear),
                    onRemove = { removeSemester(semesterNum) },
                    onTitleChange = { editSemester(semesterNum, it) }
                )
            }
            courses.forEachIndexed { index, course ->
                item {
                    CourseItemEdit(
                        title = course.title,
                        onRemove = { removeCourse(course, semesterNum) },
                        onTitleChange = { title ->
                            replaceCourseAt(index, semesterNum, Course(title))
                        }
                    )
                }
            }
            item {
                TextButtonV2(
                    onClick = { newTitleSemester = semesterNum; openDialog = true },
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Add",
                        color = MaterialTheme.colors.secondaryVariant,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
    }
    if (openDialog) {
        InputFieldDialog(
            title = "What course is this?",
            confirmTitle = "Confirm",
            dismissTitle = "Dismiss",
            onConfirm = submitNameChange,
            onDismiss = { openDialog = false }
        ) { value, onValueChange ->
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

@ExperimentalComposeUiApi
@Composable
private fun HeaderEdit(
    modifier: Modifier = Modifier,
    title: String,
    owner: String,
    updatedLast: String,
    onTitleChange: (String) -> Unit,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        onTitleChange(it)
        openDialog = false
    }

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            modifier = Modifier.clickable { openDialog = true },
            text = title,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
        )

        Text(owner, style = MaterialTheme.typography.caption)
        Text(updatedLast, style = MaterialTheme.typography.overline)

        if (openDialog) {
            InputFieldDialog(
                title = "Study Plan Title",
                confirmTitle = "Confirm",
                dismissTitle = "Dismiss",
                onConfirm = submitNameChange,
                onDismiss = { openDialog = false }
            ) { value, onValueChange ->
                InputFieldFocus { focusModifier ->
                    TextField(
                        modifier = focusModifier,
                        value = value,
                        onValueChange = onValueChange,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
                    )
                }
            }
        }
    }
}

@Composable
private fun EditActionBar(
    onProfile: () -> Unit,
    addSemester: () -> Unit,
    saveStudyPlan: () -> Unit,
    changeEditMode: (Boolean) -> Unit
) {
    IconButton(onClick = onProfile) {
        Icon(Icons.Default.PermIdentity, contentDescription = null)
    }
    IconButton(onClick = addSemester) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
    IconButton(onClick = { saveStudyPlan(); changeEditMode(false) }) {
        Icon(Icons.Default.Save, contentDescription = null)
    }
}

@Composable
private fun ActionBar(
    isAnyCollapsed: Boolean,
    toggleAllCollapsed: (Boolean) -> Unit,
    onProfile: () -> Unit,
    changeEditMode: (Boolean) -> Unit
) {
    IconButton(onClick = onProfile) {
        Icon(Icons.Default.PermIdentity, contentDescription = null)
    }
    IconButton(onClick = { toggleAllCollapsed(isAnyCollapsed) }) {
        Icon(
            imageVector = if (isAnyCollapsed) Icons.Default.Expand else Icons.Default.Compress,
            contentDescription = null
        )
    }
    IconButton(onClick = { changeEditMode(true) }) {
        Icon(Icons.Default.ModeEdit, contentDescription = null)
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@ExperimentalComposeUiApi
@Composable
private fun SemesterItemEdit(
    modifier: Modifier = Modifier,
    title: String,
    isCollapsed: Boolean = true,
    isCollapsedIcon: Painter,
    isExpandedIcon: Painter,
    color: Color,
    onRemove: (String) -> Unit,
    onTitleChange: (String) -> Unit
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        onTitleChange(it)
        openDialog = false
    }

    Box{
        Spacer(
            modifier = Modifier
                .align(Alignment.Center)
                .height(1.dp)
                .fillMaxWidth()
                .background(color)
        )
        TextButton(
            onClick = { openDialog = true },
            modifier = Modifier
                .align(Alignment.Center)
                .background(MaterialTheme.colors.surface)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colors.secondaryVariant,
                textAlign = TextAlign.Center
            )
        }
        IconButton(onClick = { onRemove(title) }, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
                contentDescription = null,
                tint = MaterialTheme.colors.error,
                painter = if (isCollapsed) isCollapsedIcon else isExpandedIcon,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .background(MaterialTheme.colors.surface)
            )
        }

        if (openDialog) {
            InputFieldDialog(
                title = "What semester is it?",
                confirmTitle = "Confirm",
                dismissTitle = "Dismiss",
                onConfirm = submitNameChange,
                onDismiss = { openDialog = false }
            ) { value, onValueChange ->
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
private fun CourseItemEdit(
    title: String,
    onRemove: (String) -> Unit,
    onTitleChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var openDialog by rememberSaveable { mutableStateOf(false) }

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
        TextButtonV2(onClick = { openDialog = true }, horizontalArrangement = Arrangement.Start) {
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle2,
            )
        }
        TextButtonV2(onClick = { onRemove(title) }, horizontalArrangement = Arrangement.End) {
            Text(
                text = "Remove",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
            )
        }
    }

    if (openDialog) {
        InputFieldDialog(
            title = "Course name",
            confirmTitle = "Confirm",
            dismissTitle = "Dismiss",
            onConfirm = submitNameChange,
            onDismiss = { openDialog = false }
        ) { value, onValueChange ->
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