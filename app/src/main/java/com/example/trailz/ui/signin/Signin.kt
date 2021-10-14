package com.example.trailz.ui.signin

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailz.R
import com.example.trailz.ui.common.compose.DividerWithText
import com.example.trailz.ui.common.compose.InputField
import com.example.trailz.ui.common.compose.InputFieldFocus

@ExperimentalComposeUiApi
@Composable
fun SignIn(
    viewModel: SigninViewModel,
    navigateUp: () -> Unit
) {
    val email by viewModel.email.observeAsState(initial = "")
    val password by viewModel.password.observeAsState(initial = "")
    val hasError by viewModel.error.observeAsState(initial = false)
    val isLoading by viewModel.loading.observeAsState(initial = false)
    val isSigninSuccess by viewModel.signinSuccess.observeAsState(initial = false)

    SideEffect { if (isSigninSuccess) navigateUp() }

    SignIn(
        email = email,
        password = password,
        onEmailChange = viewModel::changeEmail,
        onPasswordChange = viewModel::changePassword,
        isLoading = isLoading,
        hasError = hasError,
        onSignin = viewModel::signin,
        navigateUp = navigateUp
    )
}

@ExperimentalComposeUiApi
@Composable
internal fun SignIn(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    hasError: Boolean,
    onSignin: () -> Unit,
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
                title = { Text(text = "Sign In") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "We have missed you, Let's start by Sign In!",
                    style = MaterialTheme.typography.caption,
                )
            }

            item {
                InputFieldFocus { focusModifier ->
                    InputField(
                        modifier = focusModifier,
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
                }

                InputField(
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
                        onSignin()
                    })
                )
            }

            item {
                Button(
                    onClick = onSignin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(tween())
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Text(text = if (isLoading) "Loading..." else "Log In")
                }
            }

            item { DividerWithText(text = R.string.alternative_title) }

            item {
                OutlinedButton(
                    onClick = { }, modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Facebook,
                        contentDescription = null
                    )
                    Text(
                        text = "Sign in with Facebook",
                        style = MaterialTheme.typography.h6.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
