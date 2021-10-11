package com.example.trailz.ui.common.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    contentDescription: String,
    maxLines: Int = 1,
    isError: Boolean,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    imeAction: ImeAction,
    keyboardType: KeyboardType,
    onTrailingIconClicked: (() -> Unit)? = null,
    visualTransformation: VisualTransformation? = null,
    keyboardActions: KeyboardActions,
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { label?.let { Text(text = it) } },
        placeholder = { if (placeholder != null) Text(text = placeholder) },
        maxLines = maxLines,
        isError = isError,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyboardActions,
        leadingIcon = {
            leadingIcon?.let {
                Icon(it, contentDescription)
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                IconButton(onClick = { onTrailingIconClicked?.invoke() }) {
                    Icon(it, contentDescription)
                }
            }
        },
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        modifier = modifier.fillMaxWidth()
    )
}

@ExperimentalComposeUiApi
@Composable
fun InputFieldFocus(
    focusRequester: FocusRequester = FocusRequester(),
    inputField: @Composable (Modifier) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusModifier = Modifier
        .focusRequester(focusRequester)
        .onFocusEvent { if (it.hasFocus || it.isFocused) keyboardController?.show() }

    inputField(focusModifier)

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
}