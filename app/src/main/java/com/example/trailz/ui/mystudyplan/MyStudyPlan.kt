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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.base.domain.Course
import com.example.trailz.ui.common.Event
import com.example.trailz.ui.common.compose.InputFieldDialog
import com.example.trailz.ui.common.compose.InputFieldFocus
import com.example.trailz.ui.common.compose.TextButtonV2
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

    val semesterToCourses = viewModel.semesterToCourses
    val inEditMode by viewModel.inEditMode
    val isLoading by viewModel.isLoading.observeAsState(initial = true)
    val isUpdated by viewModel.isUpdated.observeAsState()

    MyStudyPlan(
        semesterToCourses = semesterToCourses,
        inEditMode = inEditMode,
        isLoading = isLoading,
        isUpdated = isUpdated,
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
        onProfile = onProfile,
        navigateUp = navigateUp
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MyStudyPlan(
    semesterToCourses: Map<Int, List<Course>>,
    inEditMode: Boolean,
    isLoading: Boolean,
    isUpdated: Event<Boolean>?,
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
    navigateUp: () -> Unit

) {
    val focusManager = LocalFocusManager.current
    val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    var openDialog by remember { mutableStateOf(false) }
    var newTitleSemester by remember { mutableStateOf(-1) }

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        addCourse(Course(it), newTitleSemester)
        openDialog = false
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "My Plan") },
                backgroundColor = MaterialTheme.colors.background,
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(imageVector = Icons.Default.PermIdentity, contentDescription = null)
                    }
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
                    IconButton(onClick = {
                        if (inEditMode) saveStudyPlan()
                        changeEditMode(!inEditMode)
                    }){
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
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            isUpdated?.contentIfNotHandled()?.let{
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = if (it) "Updated" else "Failed to update!"
                    )
                }
            }

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
                verticalArrangement = Arrangement.spacedBy(if (inEditMode) 0.dp else 12.dp),
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
                                onRemove = { removeSemester(semester) },
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
                            TextButtonV2(
                                onClick = { newTitleSemester = semester; openDialog = true },
                                horizontalArrangement = Arrangement.Start
                            ){
                                Text(
                                    text = "Add",
                                    color = MaterialTheme.colors.secondaryVariant,
                                    style = MaterialTheme.typography.caption,
                                )
                            }
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
    onRemove: (String) -> Unit,
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
        TextButton(onClick = { openDialog = true },
            modifier = Modifier
                .align(Alignment.Center)
                .background(MaterialTheme.colors.background)
        ){
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
                    .background(MaterialTheme.colors.background)
            )
        }

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
        TextButtonV2(onClick = { openDialog = true }, horizontalArrangement = Arrangement.Start){
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle2,
            )
        }
        TextButtonV2(onClick = { onRemove(title) }, horizontalArrangement = Arrangement.End){
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