package com.dtu.trailz.ui.common.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    var newTitle by rememberSaveable { mutableStateOf("") }

    val textStyle = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
    AlertDialog(
        modifier = Modifier.wrapContentHeight(unbounded = true),
        onDismissRequest = onDismiss,
        buttons = {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Text(text = title, style = textStyle)
                inputField(newTitle){ newTitle = it }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(dismissTitle)
                    }
                    TextButton(onClick = { onConfirm(newTitle) }) {
                        Text(confirmTitle)
                    }
                }
            }
        },
    )
}