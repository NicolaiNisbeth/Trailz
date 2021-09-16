package com.example.trailz.ui.signin

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailz.R
import com.example.trailz.ui.common.compose.DividerWithText
import com.example.trailz.ui.common.compose.InputFiled
import com.example.trailz.ui.common.compose.invalidInput

@Composable
fun Login(
    onLoginSuccess: () -> Unit,
) {

    var hasError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Login(
        hasError = hasError,
        loading = loading,
        onLogin = { email, password ->
            if (invalidInput(email, password)) {
                hasError = true
                loading = false
            } else {
                hasError = false
                loading = true
                onLoginSuccess()
            }
        }
    )
}

@Composable
internal fun Login(
    hasError: Boolean,
    loading: Boolean,
    onLogin: (String, String) -> Unit,
){
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisibility by remember { mutableStateOf(false) }

    val (passwordIcon, passwordTransformation) = if (passwordVisibility){
        Icons.Filled.Visibility to VisualTransformation.None
    } else {
        Icons.Filled.VisibilityOff to PasswordVisualTransformation()
    }

    Scaffold {
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
                InputFiled(
                    value = email,
                    onValueChange = { email = it},
                    label = "Email address",
                    placeholder = "abc@gmail.com",
                    contentDescription = "Email address",
                    isError = hasError,
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    leadingIcon = rememberVectorPainter(Icons.Default.Email),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )

                InputFiled(
                    value = password,
                    onValueChange = { password = it },
                    label = "password",
                    placeholder = "qwert12345",
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
                        onLogin(email.text, password.text)
                    })
                )
            }

            item {
                Button(
                    onClick = { onLogin(email.text, password.text) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(tween())
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Text(text = if (loading) "Loading..." else "Log In")
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
