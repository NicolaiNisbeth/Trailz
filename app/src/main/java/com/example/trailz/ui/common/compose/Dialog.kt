package com.example.trailz.ui.common.compose

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@ExperimentalComposeUiApi
@Composable
fun InputFieldDialog(
    title: String,
    confirmTitle: String,
    dismissTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    inputField: @Composable (String, (String) -> Unit) -> Unit
) {
    var newTitle by remember { mutableStateOf("") }

    val textStyle = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
    AlertDialog(
        title = { Text(text = title, style = textStyle) },
        text = {
            inputField(newTitle){
                newTitle = it
            }
       },
        confirmButton = {
            Button(onClick = { onConfirm(newTitle) }) {
                Text(confirmTitle)
            }
        },
        dismissButton = {
            Button(onDismiss) {
                Text(dismissTitle)
            }
        },
        onDismissRequest = onDismiss,
    )
}