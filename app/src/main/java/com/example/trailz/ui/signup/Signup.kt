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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trailz.ui.common.compose.InputFiled
import com.example.trailz.ui.common.compose.invalidInput
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import android.annotation.SuppressLint
import com.google.android.material.animation.AnimationUtils
import androidx.compose.material.icons.filled.FlutterDash
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.math.absoluteValue
import com.google.accompanist.pager.calculateCurrentOffsetForPage

data class UserCredential(
    val username: String,
    val email: String,
    val password: String,
    val studyPath: String,
)

@ExperimentalPagerApi
@Composable
fun Signup(
    onSignupSuccess: () -> Unit,
) {

    var hasError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val studyPaths = listOf(
        "Softwareteknologi" to rememberVectorPainter(image = Icons.Default.FlutterDash),
        "Produktion" to rememberVectorPainter(image = Icons.Default.Psychology),
        "Elektroteknologi" to rememberVectorPainter(image = Icons.Default.Paid),
    )

    Signup(
        studyPaths = studyPaths,
        hasError = hasError,
        loading = loading,
        onSignup = { (_, email, password, _) ->
            if (invalidInput(email, password)) {
                hasError = true
                loading = false
            } else {
                hasError = false
                loading = true
                onSignupSuccess()
            }
        }
    )
}

@ExperimentalPagerApi
@Composable
internal fun Signup(
    studyPaths: List<Pair<String, Painter>>,
    hasError: Boolean,
    loading: Boolean,
    onSignup: (UserCredential) -> Unit,
){
    val focusManager = LocalFocusManager.current
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisibility by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        pageCount = studyPaths.count(),
        initialOffscreenLimit = 2,
        infiniteLoop = true
    )

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
                    text = "Welcome",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "Create your account",
                    style = MaterialTheme.typography.caption,
                )
            }

            item {
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = pagerState,
                ) { page ->
                    PagerItem(
                        modifier = Modifier.height(150.dp).fillMaxWidth(0.5f),
                        item = studyPaths[page],
                        pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    )
                }
            }

            item {
                InputFiled(
                    value = username,
                    onValueChange = { username = it},
                    label = "Username",
                    placeholder = "abc",
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
                        onSignup(
                            UserCredential(
                                username = username.text,
                                email = username.text,
                                password = username.text,
                                studyPath = ""
                            )
                        )
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
                    onClick = {
                        onSignup(
                            UserCredential(
                                username = username.text,
                                email = username.text,
                                password = username.text,
                                studyPath = ""
                            )
                        )
                    },
                ) {
                    Text(text = if (loading) "Loading..." else "Sign up")
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
internal fun PagerItem(
    modifier: Modifier,
    item: Pair<String, Painter>,
    pageOffset: Float
) {
    val (title, icon) = item
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

                // We animate the alpha, between 50% and 100%
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
                painter = icon,
                contentDescription = "study path image"
            )
            Text(
                modifier = Modifier,
                text = title,
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
    }
}
