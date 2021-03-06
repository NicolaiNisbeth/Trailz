package com.dtu.trailz.ui.login.signup

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dtu.trailz.ui.common.compose.InputField
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import com.dtu.trailz.R
import com.dtu.trailz.ui.common.compose.InputFieldFocus
import com.google.accompanist.pager.PagerState

@ExperimentalComposeUiApi
@ExperimentalPagerApi
@Composable
fun SignUp(
    viewModel: SignupViewModel,
    navigateUp: () -> Unit,
    popLogin: () -> Unit
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

    SignUp(
        username = username,
        email = email,
        password = password,
        studyPaths = studyPaths,
        pagerState = pagerState,
        isSignUpSuccess = isSignupSuccess,
        onUsernameChange = viewModel::changeUsername,
        onEmailChange = viewModel::changeEmail,
        onPasswordChange = viewModel::changePassword,
        onStudyPathChange = viewModel::changeStudyPath,
        hasError = hasError,
        isLoading = isLoading,
        onSignup = viewModel::signUp,
        navigateUp = navigateUp,
        popLogin = popLogin
    )
}

@ExperimentalComposeUiApi
@ExperimentalPagerApi
@Composable
internal fun SignUp(
    username: String,
    email: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onStudyPathChange: (String) -> Unit,
    pagerState: PagerState,
    studyPaths: List<String>,
    isSignUpSuccess: Boolean,
    hasError: Boolean,
    isLoading: Boolean,
    onSignup: () -> Unit,
    navigateUp: () -> Unit,
    popLogin: () -> Unit
){
    val focusManager = LocalFocusManager.current
    var passwordVisibility by remember { mutableStateOf(false) }

    val (passwordIcon, passwordTransformation) = if (passwordVisibility){
        Icons.Filled.Visibility to VisualTransformation.None
    } else {
        Icons.Filled.VisibilityOff to PasswordVisualTransformation()
    }

    if (isSignUpSuccess) {
        LocalSoftwareKeyboardController.current?.hide()
        popLogin()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sign_up_cta)) },
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
                    text = stringResource(R.string.sign_up_title),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = stringResource(R.string.sign_up_description),
                    style = MaterialTheme.typography.caption,
                )
            }

            item {
                InputFieldFocus() {
                    InputField(
                        modifier = it,
                        value = username,
                        onValueChange = onUsernameChange,
                        label = stringResource(R.string.sign_up_username),
                        contentDescription = stringResource(R.string.sign_up_username),
                        isError = hasError,
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                        leadingIcon = rememberVectorPainter(Icons.Default.Person),
                        keyboardCapitalization = KeyboardCapitalization.Sentences,
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        })
                    )
                }

                InputField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.sign_up_email),
                    contentDescription = stringResource(R.string.sign_up_email),
                    isError = hasError,
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    leadingIcon = rememberVectorPainter(Icons.Default.Email),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )

                InputField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.sign_up_password),
                    contentDescription = stringResource(R.string.sign_up_password),
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
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(tween())
                        .height(50.dp)
                        .clip(CircleShape),
                    onClick = onSignup
                ) {
                    Text(text = if (isLoading) stringResource(R.string.sign_up_loading) else stringResource(R.string.sign_in_cta))
                }
            }
        }
    }
}
