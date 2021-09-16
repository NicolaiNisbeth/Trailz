package com.example.trailz.ui.common.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView

@Composable
fun LottieView(
    modifier: Modifier = Modifier,
    context: Context,
    animationFile: String,
    repeatFor: Int
) {
    val lottieView = remember {
        LottieAnimationView(context).apply {
            setAnimation(animationFile)
            repeatCount = repeatFor
        }
    }
    AndroidView(
        factory = { lottieView },
        modifier = modifier,
        update = { it.playAnimation() }
    )
}