package com.example.trailz.ui.studyplan.mystudyplan

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.base.domain.Course
import com.example.trailz.R
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.compose.ExpandableContent
import com.example.trailz.ui.common.compose.InputFieldDialog
import com.example.trailz.ui.common.compose.InputFieldFocus
import com.example.trailz.ui.common.compose.TextButtonV2
import com.example.trailz.ui.common.studyplan.CourseItem
import com.example.trailz.ui.common.studyplan.SemesterItem
import com.example.trailz.ui.common.studyplan.SemesterList
import com.example.trailz.ui.studyplan.MyStudyPlanData
import com.example.trailz.ui.studyplan.StudyPlanViewModel
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MyStudyPlan(
    viewModel: StudyPlanViewModel,
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

@ExperimentalAnimationApi
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
    val lazyListState = rememberLazyListState()

    state.data?.isUpdated?.contentIfNotHandled()?.let {
        val msg =
            if (it) stringResource(R.string.my_study_plan_saved) else stringResource(R.string.my_study_plan_saved_fail)
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.my_study_plan_title)) },
                backgroundColor = MaterialTheme.colors.background,
                actions = {
                    state.data?.let {
                        if (it.inEditMode) {
                            EditActionBar(
                                onProfile = onProfile,
                                saveStudyPlan = saveStudyPlan,
                                changeEditMode = changeEditMode,
                                addSemester = addSemester
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
                        lazyListState = lazyListState,
                        title = it.title,
                        username = stringResource(R.string.my_study_plan_creator, formatArgs = arrayOf(it.username)),
                        updated = it.updatedLast,
                        editStudyPlanTitle = editStudyPlanTitle,
                        semesterToCourses = semesterToCourses,
                        isSemesterCollapsed = isSemesterCollapsed,
                        removeCourse = removeCourse,
                        removeSemester = removeSemester,
                        editSemester = editSemester,
                        replaceCourseAt = replaceCourseAt,
                        addCourse = addCourse,
                        expandSemester = expandSemester,
                        collapsSemester = collapseSemester
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
                        username = stringResource(R.string.my_study_plan_creator, formatArgs = arrayOf(it.username)),
                        updatedLast = it.updatedLast,
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

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun SemesterListEdit(
    lazyListState: LazyListState = rememberLazyListState(),
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
    expandSemester: (Int) -> Unit,
    collapsSemester: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var newTitleSemester by rememberSaveable { mutableStateOf(-1) }

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        addCourse(Course(it.trim()), newTitleSemester)
        openDialog = false
    }

    val list = semesterToCourses.toSortedMap().toList()

    LazyColumn(
        state = lazyListState,
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

        items(list.size) { idx ->
            val (semester, courses) = list[idx]
            val isCollapsed = isSemesterCollapsed(semester)
            ExpandableContent(
                isExpanded = isCollapsed.not(),
                fixedContent = { rotationDegree ->
                    SemesterItemEdit(
                        title = "$semester",
                        rotationDegree = rotationDegree,
                        color = MaterialTheme.colors.primary,
                        isExpandedIcon = rememberVectorPainter(image = Icons.Default.KeyboardArrowUp),
                        onRemove = { removeSemester(semester) },
                        onTitleChange = { editSemester(semester, it) },
                        onToggleSemester = {
                            if (isCollapsed) expandSemester(semester) else collapsSemester(
                                semester
                            )
                        }
                    )
                },
                expandedContent = {
                    Column {
                        courses.forEachIndexed { index, course ->
                            CourseItemEdit(
                                title = course.title,
                                onRemove = { removeCourse(course, semester) },
                                onTitleChange = { title ->
                                    replaceCourseAt(index, semester, Course(title))
                                }
                            )
                        }
                        TextButtonV2(
                            onClick = { newTitleSemester = semester; openDialog = true },
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = stringResource(R.string.my_study_plan_add),
                                color = MaterialTheme.colors.secondaryVariant,
                                style = MaterialTheme.typography.caption,
                            )
                        }
                    }
                }
            )
        }
    }
    if (openDialog) InputFieldDialog(
        title = stringResource(R.string.my_study_plan_dialog_course_title),
        confirmTitle = stringResource(R.string.my_study_plan_dialog_confirm),
        dismissTitle = stringResource(R.string.my_study_plan_dialog_dismiss),
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
                    keyboardType = KeyboardType.Password,
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = true
                ),
                keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
            )
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
        onTitleChange(it.trim())
        openDialog = false
    }

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(updatedLast, style = MaterialTheme.typography.overline)
        Text(
            text = title,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { openDialog = true }
        )
        Text(owner, style = MaterialTheme.typography.caption)

        if (openDialog) InputFieldDialog(
            title = stringResource(R.string.my_study_plan_dialog_plan_title),
            confirmTitle = stringResource(R.string.my_study_plan_dialog_confirm),
            dismissTitle = stringResource(R.string.my_study_plan_dialog_dismiss),
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
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true
                    ),
                    keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
                )
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
    rotationDegree: Float,
    isExpandedIcon: Painter,
    color: Color,
    onRemove: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onToggleSemester: () -> Unit
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val submitNameChange: (String) -> Unit = {
        focusManager.clearFocus()
        onTitleChange(it.trim())
        openDialog = false
    }

    Box(modifier) {
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
                painter = rememberVectorPainter(image = Icons.Default.Clear),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .background(MaterialTheme.colors.surface)
            )
        }
        IconButton(
            onClick = onToggleSemester,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                contentDescription = null,
                tint = color,
                painter = isExpandedIcon,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .rotate(rotationDegree)
                    .background(MaterialTheme.colors.surface)
            )
        }

        if (openDialog) InputFieldDialog(
            title = stringResource(R.string.my_study_plan_dialog_semester_title),
            confirmTitle = stringResource(R.string.my_study_plan_dialog_confirm),
            dismissTitle = stringResource(R.string.my_study_plan_dialog_dismiss),
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
                        keyboardType = KeyboardType.Number,
                    ),
                    keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
                )
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
        onTitleChange(it.trim())
        openDialog = false
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (titleRef, removeRef) = createRefs()
        TextButtonV2(
            onClick = { openDialog = true },
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(removeRef.start)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle2,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        TextButtonV2(
            onClick = { onRemove(title) },
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.constrainAs(removeRef) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(titleRef.end)
            }
        ) {
            Text(
                text = stringResource(R.string.my_study_plan_course_remove),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
            )
        }
    }

    if (openDialog) InputFieldDialog(
        title = stringResource(R.string.my_study_plan_dialog_course_name),
        confirmTitle = stringResource(R.string.my_study_plan_dialog_confirm),
        dismissTitle = stringResource(R.string.my_study_plan_dialog_dismiss),
        onConfirm = submitNameChange,
        onDismiss = { openDialog = false }
    ) { value, onValueChange ->
        InputFieldFocus { focusModifier ->
            TextField(
                modifier = focusModifier.height(IntrinsicSize.Max),
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = true
                ),
                keyboardActions = KeyboardActions(onDone = { submitNameChange(value) }),
            )
        }
    }
}