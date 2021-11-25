package com.dtu.trailz.ui.common.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.dtu.trailz.R

/**
 * https://gist.github.com/vitorprado/0ae4ad60c296aefafba4a157bb165e60
 */
@ExperimentalComposeUiApi
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            val (icon, color) = if (i <= rating){
                R.drawable.ic_baseline_star_rate_24 to Color(0xFFFFD700)
            } else {
                R.drawable.ic_baseline_star_outline_24 to Color(0xFFA2ADB1)
            }
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "star",
                tint = color
            )
        }
    }
}