package com.example.trailz.ui.profile

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.trailz.R
import com.example.trailz.language.LanguageConfig
import com.example.trailz.ui.common.compose.RatingBar
import com.example.trailz.ui.signup.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun Profile(
    viewModel: ProfileViewModel,
    appliedCountry: LanguageConfig,
    onChangeLanguage: (String) -> Unit,
    navigateUp: () -> Unit,
    signIn: () -> Unit,
    signUp: () -> Unit,
    rateApp: () -> Unit,
    settings: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val uiState by viewModel.state.observeAsState(initial = ProfileUiState(isLoading = true))

    Profile(
        state = uiState,
        coroutineScope = scope,
        modalBottomSheetState = modalBottomSheetState,
        appliedCountry = appliedCountry,
        onChangeLanguage = onChangeLanguage,
        navigateUp = navigateUp,
        signIn = signIn,
        signUp = signUp,
        logout = viewModel::logout,
        rateApp = rateApp,
        settings = settings
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun Profile(
    state: ProfileUiState,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    appliedCountry: LanguageConfig,
    onChangeLanguage: (String) -> Unit,
    navigateUp: () -> Unit,
    signIn: () -> Unit,
    signUp: () -> Unit,
    logout: () -> Unit,
    rateApp: () -> Unit,
    settings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            modalBottomSheetState.animateTo(
                                targetValue = ModalBottomSheetValue.Expanded,
                                anim = tween(800)
                            )
                        }
                    }) {
                        Image(painterResource(id = appliedCountry.flagResource), contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        ModalBottomSheetLayout(
            modifier = Modifier.padding(paddingValues),
            sheetState = modalBottomSheetState,
            sheetContent = {
                LanguageView(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp), onChangeLanguage, appliedCountry)
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator()
                    state.isLoggedIn -> LoggedInView(state.user!!, logout)
                    else -> LoggedOutView(signUp, signIn, rateApp, settings)
                }
            }
        }
    }
}

@Composable
fun LanguageView(
    modifier: Modifier = Modifier,
    onChangeLanguage: (String) -> Unit,
    appliedCountry: LanguageConfig
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sprog",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        LanguageConfig.values().forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChangeLanguage(it.code) }
            ) {
                Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(id = it.flagResource), null)
                    Spacer(Modifier.width(18.dp))
                    Text(it.title, textAlign = TextAlign.Center)
                }
                if (it.code == appliedCountry.code) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun LoggedInView(
    user: User,
    logout: () -> Unit
) {
    Column {
        Text(text = user.toString())
        Button(onClick = logout) {
            Text(text = "Logout")
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun LoggedOutView(
    signUp: () -> Unit,
    signIn: () -> Unit,
    rateApp: () -> Unit,
    settings: () -> Unit
) {
    
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LoginHeader(
            onSignIn = signIn,
            onSignUp = signUp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(Color.LightGray.copy(alpha = 0.1f))
        )
        RateAppView(
            onRateApp = rateApp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
        )
        SettingsView(
            onSettings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
        )
    }
}

@Composable
fun SettingsView(
    modifier: Modifier = Modifier,
    onSettings: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onSettings() }
            .padding(16.dp)
    ) {
        Text(text = "Settings")
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
    }
}

@Composable
fun LoginHeader(
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit
) {
    ConstraintLayout(modifier) {
        val (signInBtn, signUpBtn, description) = createRefs()
        Text(
            textAlign = TextAlign.Center,
            text = "Se dine favoritter fra alle enheder",
            modifier = Modifier.constrainAs(description){
                top.linkTo(parent.top)
                start.linkTo(signInBtn.start)
                end.linkTo(signUpBtn.end)
                bottom.linkTo(signUpBtn.top)
            }
        )
        Button(
            onClick = onSignIn,
            modifier = Modifier.constrainAs(signInBtn){
                start.linkTo(parent.start)
                end.linkTo(signUpBtn.start)
                bottom.linkTo(parent.bottom)
                top.linkTo(description.bottom)
            }
        ) {
            Text(text = "Sign in")
        }

        Button(
            onClick = onSignUp,
            modifier = Modifier.constrainAs(signUpBtn){
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                start.linkTo(signInBtn.end)
                top.linkTo(description.bottom)
            }
        ) {
            Text(text = "Sign up")
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun RateAppView(
    modifier: Modifier = Modifier,
    onRateApp: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onRateApp() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Bedøm vores app")
        RatingBar(rating = 3)
    }
}