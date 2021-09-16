package com.example.trailz.ui.common.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DividerWithText(
    modifier: Modifier = Modifier,
    @StringRes text: Int,
    color: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.disabled)
){
    Box(modifier) {
        Spacer(
            modifier = Modifier
                .align(Alignment.Center)
                .height(1.dp)
                .fillMaxWidth()
                .background(color)
        )
        Text(
            text = stringResource(id = text),
            color = color,
            modifier = Modifier
                .align(Alignment.Center)
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 16.dp)
        )
    }
}
