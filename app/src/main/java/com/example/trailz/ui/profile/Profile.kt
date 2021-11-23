package com.example.trailz.ui.profile

import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trailz.R
import com.example.trailz.language.LanguageConfig
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.compose.RatingBar
import com.example.trailz.ui.favorites.LoadingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun Profile(
    viewModel: ProfileViewModel,
    appliedCountry: LanguageConfig,
    isDarkTheme: Boolean,
    onChangeLanguage: (String) -> Unit,
    navigateUp: () -> Unit,
    toggleTheme: (Boolean) -> Unit,
    logout: () -> Unit,
    rateApp: () -> Unit,
    settings: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val uiState by viewModel.state.observeAsState(initial = DataState(isLoading = true))

    Profile(
        state = uiState,
        coroutineScope = scope,
        isDarkTheme = isDarkTheme,
        modalBottomSheetState = modalBottomSheetState,
        appliedCountry = appliedCountry,
        onChangeLanguage = onChangeLanguage,
        navigateUp = navigateUp,
        toggleTheme = toggleTheme,
        logout = logout,
        rateApp = rateApp,
        settings = settings
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun Profile(
    state: DataState<ProfileData>,
    coroutineScope: CoroutineScope,
    isDarkTheme: Boolean,
    modalBottomSheetState: ModalBottomSheetState,
    appliedCountry: LanguageConfig,
    onChangeLanguage: (String) -> Unit,
    navigateUp: () -> Unit,
    toggleTheme: (Boolean) -> Unit,
    logout: () -> Unit,
    rateApp: () -> Unit,
    settings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.profile_toolbar_title)) },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            modalBottomSheetState.animateTo(
                                targetValue = ModalBottomSheetValue.Expanded,
                                anim = tween(500)
                            )
                        }
                    }) {
                        Image(
                            painterResource(id = appliedCountry.flagResource),
                            contentDescription = null
                        )
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    onChangeLanguage = onChangeLanguage,
                    appliedCountry = appliedCountry
                )
            }
        ) {
            if (state.isLoading) {
                LoadingScreen()
            }

            val data = state.data
            if (data != null) {
                LoggedInView(
                    user = data.user,
                    likes = data.likes,
                    isDarkTheme = isDarkTheme,
                    logout = logout,
                    rateApp = rateApp,
                    settings = settings,
                    toggleTheme = toggleTheme
                )
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
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.language_title),
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

@ExperimentalComposeUiApi
@Composable
fun LoggedInView(
    user: com.example.base.domain.User,
    likes: Long,
    isDarkTheme: Boolean,
    logout: () -> Unit,
    rateApp: () -> Unit,
    settings: () -> Unit,
    toggleTheme: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.profile_title, formatArgs = arrayOf(user.username)),
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$likes",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.user_likes),
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = stringResource(R.string.title_profile),
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp),
            textAlign = TextAlign.Center
        )

        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.profile_field_email),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = user.email,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.profile_field_password),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "****",
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.profile_field_degree),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = user.degree,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = stringResource(R.string.profile_field_about),
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp),
            textAlign = TextAlign.Center
        )

        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            RateAppView(
                modifier = Modifier.fillMaxWidth(),
                onRateApp = rateApp,
            )
            SettingsView(
                modifier = Modifier.fillMaxWidth(),
                onSettings = settings,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isDarkTheme) stringResource(R.string.profile_theme_light_cta) else stringResource(
                        R.string.profile_theme_dark_cta
                    )
                )
                Switch(isDarkTheme, toggleTheme)
            }
        }

        Text(
            text = stringResource(R.string.profile_logout_cta),
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .clickable { logout() }
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
        Text(text = stringResource(R.string.profile_settings_cta))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
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
        Text(text = stringResource(R.string.profile_review_cta))
        RatingBar(rating = 4)
    }
}