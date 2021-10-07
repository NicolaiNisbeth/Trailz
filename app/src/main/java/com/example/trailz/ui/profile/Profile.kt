package com.example.trailz.ui.profile

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trailz.R
import com.example.trailz.language.LanguageConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun Profile(
    viewModel: ProfileViewModel,
    appliedCountry: LanguageConfig,
    onChangeLanguage: (String) -> Unit,
    navigateUp: () -> Unit,
    signIn: () -> Unit,
    signUp: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    Profile(
        coroutineScope = scope,
        modalBottomSheetState = modalBottomSheetState,
        appliedCountry = appliedCountry,
        onChangeLanguage = onChangeLanguage,
        navigateUp = navigateUp,
        signIn = signIn,
        signUp = signUp,
        logout = viewModel::logout
    )
}

@ExperimentalMaterialApi
@Composable
fun Profile(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    appliedCountry: LanguageConfig,
    onChangeLanguage: (String) -> Unit,
    navigateUp: () -> Unit,
    signIn: () -> Unit,
    signUp: () -> Unit,
    logout: () -> Unit
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
                                anim = tween(600)
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
            Column {
                Row {
                    Button(onClick = signIn) {
                        Text(text = "Sign in")
                    }
                    Button(onClick = signUp) {
                        Text(text = "Sign up")
                    }
                }
            }
        }
    }
}