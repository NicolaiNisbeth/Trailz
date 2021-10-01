package com.example.trailz.ui.signup

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trailz.ui.common.compose.InputFiled
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import com.google.android.material.animation.AnimationUtils
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.PagerState
import kotlin.math.absoluteValue
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import kotlinx.coroutines.flow.collect

@ExperimentalPagerApi
@Composable
fun Signup(
    viewModel: SignupViewModel,
    navigateUp: () -> Unit
) {
    val username by viewModel.username.observeAsState(initial = "")
    val email by viewModel.email.observeAsState(initial = "")
    val password by viewModel.password.observeAsState(initial = "")
    val studyPath by viewModel.studyPath.observeAsState(initial = "Softwareteknologi")
    val studyPaths by viewModel.studyPaths.observeAsState(initial = emptyList())
    val isLoading by viewModel.loading.observeAsState(initial = false)
    val hasError by viewModel.error.observeAsState(initial = false)
    val isSignupSuccess by viewModel.signupSuccess.observeAsState(initial = false)

    val pagerState = rememberPagerState(
        pageCount = studyPaths.count(),
        initialOffscreenLimit = 2,
        infiniteLoop = true,
        initialPage = studyPaths
            .indexOfFirst { it == studyPath }
            .takeUnless { it == -1 } ?: 0
    )

    SideEffect { if (isSignupSuccess) navigateUp() }

    Signup(
        username = username,
        email = email,
        password = password,
        studyPaths = studyPaths,
        pagerState = pagerState,
        onUsernameChange = viewModel::changeUsername,
        onEmailChange = viewModel::changeEmail,
        onPasswordChange = viewModel::changePassword,
        onStudyPathChange = viewModel::changeStudyPath,
        hasError = hasError,
        isLoading = isLoading,
        onSignup = viewModel::signup,
        navigateUp = navigateUp
    )
}

@ExperimentalPagerApi
@Composable
internal fun Signup(
    username: String,
    email: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onStudyPathChange: (String) -> Unit,
    pagerState: PagerState,
    studyPaths: List<String>,
    hasError: Boolean,
    isLoading: Boolean,
    onSignup: () -> Unit,
    navigateUp: () -> Unit
){
    val focusManager = LocalFocusManager.current
    var passwordVisibility by remember { mutableStateOf(false) }

    val (passwordIcon, passwordTransformation) = if (passwordVisibility){
        Icons.Filled.Visibility to VisualTransformation.None
    } else {
        Icons.Filled.VisibilityOff to PasswordVisualTransformation()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sign Up") },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    }
                },
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "Create your account",
                    style = MaterialTheme.typography.caption,
                )
            }

            item {
                InputFiled(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = "Username",
                    contentDescription = "Username",
                    isError = hasError,
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    leadingIcon = rememberVectorPainter(Icons.Default.Person),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )

                InputFiled(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email address",
                    contentDescription = "Email address",
                    isError = hasError,
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    leadingIcon = rememberVectorPainter(Icons.Default.Email),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )

                InputFiled(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "password",
                    contentDescription = "password",
                    isError = hasError,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                    leadingIcon = rememberVectorPainter(Icons.Default.VpnKey),
                    trailingIcon = rememberVectorPainter(passwordIcon),
                    visualTransformation = passwordTransformation,
                    onTrailingIconClicked = { passwordVisibility = !passwordVisibility },
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onSignup()
                    })
                )
            }

            item {
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = pagerState.apply {
                        // listen on page changes
                        LaunchedEffect(this) {
                            snapshotFlow { currentPage }.collect { index ->
                                onStudyPathChange(studyPaths[index])
                            }
                        }
                    },
                ) { page ->
                    PagerItem(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(0.5f),
                        item = studyPaths[page],
                        pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    )
                }
            }

            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(tween())
                        .height(50.dp)
                        .clip(CircleShape),
                    onClick = onSignup
                ) {
                    Text(text = if (isLoading) "Loading..." else "Sign up")
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
internal fun PagerItem(
    modifier: Modifier,
    item: String,
    pageOffset: Float
) {
    Card(
        elevation = 0.dp,
        modifier = modifier
            .graphicsLayer {
                AnimationUtils
                    .lerp(
                        0.85f,
                        1f,
                        1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }

                alpha = AnimationUtils.lerp(
                    0.5f,
                    1f,
                    1f - pageOffset.coerceIn(0f, 1f)
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(0.8f),
                painter = rememberVectorPainter(image = Icons.Default.FlutterDash),
                contentDescription = "study path image"
            )
            Text(
                modifier = Modifier,
                text = item,
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
    }
}
