package com.example.trailz.ui.common.compose

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontWeight

@ExperimentalComposeUiApi
@Composable
fun InputFieldDialog(
    title: String,
    confirmTitle: String,
    dismissTitle: String,
    onTitleChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newTitle by remember { mutableStateOf("") }

    AlertDialog(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
            )
        },
        text = {
            InputFieldFocus(
                title = newTitle,
                titleChange = { newTitle = it },
                onDone = {
                    onTitleChange(newTitle)
                    onDismiss()
                }
            )
        },
        confirmButton = {
            Button(onClick = {
                onTitleChange(newTitle)
                onDismiss()
            }) {
                Text(confirmTitle)
            }
        },
        dismissButton = {
            Button(onDismiss) { Text(dismissTitle) }
        },
        onDismissRequest = onDismiss,
    )
}