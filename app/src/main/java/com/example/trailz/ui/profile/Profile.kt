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
                    onChangeLanguage = onChangeLanguage,
                    appliedCountry = appliedCountry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
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
            .verticalScroll(rememberScrollState())
    ) {
        Header(
            username = user.username,
            likes = likes
        )
        ProfileSection(
            email = user.email,
            degree = user.degree
        )
        Spacer(Modifier.height(16.dp))
        GeneralSection(
            rateApp = rateApp,
            settings = settings,
            isDarkTheme = isDarkTheme,
            toggleTheme = toggleTheme,
        )
        Text(
            text = stringResource(R.string.profile_logout_cta),
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable { logout() }
                .padding(16.dp)
        )
    }
}

@Composable
private fun Header(
    username: String,
    likes: Long
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_title, formatArgs = arrayOf(username)),
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
}

@Composable
private fun ProfileSection(
    email: String,
    degree: String
) {
    Text(
        text = stringResource(R.string.title_profile),
        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(start = 16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {
        RowItem(
            startTitle = stringResource(R.string.profile_field_email),
            endContent = {
                Text(
                    text = email,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center
                )
            }
        )
        RowItem(
            startTitle = stringResource(R.string.profile_field_password),
            endContent = {
                Text(
                    text = "*****",
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5
                )
            }
        )
        RowItem(
            startTitle = stringResource(R.string.profile_field_degree),
            endContent = {
                Text(
                    text = degree,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun GeneralSection(
    rateApp: () -> Unit,
    settings: () -> Unit,
    isDarkTheme: Boolean,
    toggleTheme: (Boolean) -> Unit,
) {
    Text(
        text = stringResource(R.string.profile_field_about),
        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(start = 16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {
        RowItem(
            modifier = Modifier.clickable { rateApp() },
            startTitle = stringResource(R.string.profile_review_cta),
            endContent = { RatingBar(rating = 4) }
        )
        RowItem(
            modifier = Modifier.clickable { settings() },
            startTitle = stringResource(R.string.profile_settings_cta),
            endContent = { Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null) }
        )
        RowItem(
            startTitle = if (isDarkTheme) stringResource(R.string.profile_theme_light_cta) else stringResource(R.string.profile_theme_dark_cta),
            endContent = { Switch(isDarkTheme, toggleTheme) }
        )
    }
}

@Composable
private fun RowItem(
    modifier: Modifier = Modifier,
    startTitle: String,
    endContent: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(startTitle, textAlign = TextAlign.Center)
        endContent()
    }
}
