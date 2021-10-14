package com.example.trailz.ui.profile

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
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
    rateApp: () -> Unit
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
        rateApp = rateApp
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
    rateApp: () -> Unit
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Sprog")
                    LanguageConfig.values().forEach {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChangeLanguage(it.code) }
                        ) {

                            Image(painterResource(id = it.flagResource), contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = it.title, modifier = Modifier.align(Alignment.CenterVertically))
                            if (it.code == appliedCountry.code) {
                                Icon(imageVector = Icons.Default.Done, contentDescription = null)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(78.dp))
                }
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator()
                    state.isLoggedIn -> LoggedInView(state.user!!, logout)
                    else -> LoggedOutView(signUp, signIn, rateApp)
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
    rateApp: () -> Unit
) {
    
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        LoginHeader(
            onSignIn = signIn,
            onSignUp = signUp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(Color.LightGray.copy(alpha = 0.1f))
        )
        Spacer(Modifier.height(16.dp))
        RateAppView(
            onRateApp = rateApp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
        )
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
        modifier = modifier.clickable { onRateApp() }.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Bedøm vores app")
        RatingBar(rating = 3)
    }
}