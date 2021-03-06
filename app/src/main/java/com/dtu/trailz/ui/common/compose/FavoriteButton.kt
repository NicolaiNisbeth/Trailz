package com.dtu.trailz.ui.common.compose

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    colorOnChecked: Color,
    colorUnChecked: Color,
    onClick: (Boolean) -> Unit
) {
    IconToggleButton(
        modifier = modifier.testTag("favoriteButton"),
        checked = isChecked,
        onCheckedChange = onClick
    ) {
        val transition = updateTransition(isChecked, label = "Checked indicator")

        val tint by transition.animateColor(label = "ColorAnimation") { isChecked ->
            if (isChecked) colorOnChecked else colorUnChecked
        }

        val size by transition.animateDp(
            label = "DpAnimation",
            targetValueByState = { 30.dp },
            transitionSpec = {
                keyframes {
                    durationMillis = 250
                    30.dp at 0 with LinearOutSlowInEasing // for 0-15 ms
                    35.dp at 15 with FastOutLinearInEasing // for 15-75 ms
                    40.dp at 75 // ms
                    35.dp at 150 // ms
                }
            }
        )

        Icon(
            imageVector = if (isChecked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size).testTag("favoriteIcon")
        )
    }
}