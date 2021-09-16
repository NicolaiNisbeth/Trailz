package com.example.trailz.ui.common.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputFiled(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    contentDescription: String,
    maxLines: Int = 1,
    isError: Boolean,
    leadingIcon: Painter,
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
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        maxLines = maxLines,
        isError = isError,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyboardActions,
        leadingIcon = { Icon(leadingIcon, contentDescription) },
        trailingIcon = {
            trailingIcon?.let {
                IconButton(onClick = { onTrailingIconClicked?.invoke() }) {
                    Icon(it, contentDescription)
                }
            }
        },
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        modifier = Modifier.fillMaxWidth()
    )
}