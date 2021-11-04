package com.example.trailz.ui.common.compose

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val EXPAND_ANIMATION_DURATION = 200
val FADE_IN_ANIMATION_DURATION = 200
val FADE_OUT_ANIMATION_DURATION = 200
val COLLAPSE_ANIMATION_DURATION = 200

@ExperimentalAnimationApi
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    fixedContent: @Composable (arrowRotationDegree: Float) -> Unit,
    expandableContent: @Composable ()  -> Unit
) {
    val transition = updateTransition(
        label = "expandTransition",
        transitionState = remember {
            MutableTransitionState(isExpanded).apply {
                targetState = !isExpanded
            }
        }
    )

    val cardElevation by transition.animateDp(
        transitionSpec = { tween(durationMillis = EXPAND_ANIMATION_DURATION) },
        targetValueByState = { if (isExpanded) 24.dp else 4.dp },
        label = "cardElevation"
    )

    val arrowRotationDegree by transition.animateFloat(
        transitionSpec = { tween(durationMillis = EXPAND_ANIMATION_DURATION) },
        targetValueByState = { if (isExpanded) 0f else 180f },
        label = "arrowRotation"
    )

    Card(
        elevation = cardElevation,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            fixedContent(arrowRotationDegree)
            ExpandableContent(isVisible = isExpanded, initialVisibility = isExpanded) {
                expandableContent()
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun ExpandableContent(
    isVisible: Boolean,
    initialVisibility: Boolean,
    content: @Composable () -> Unit
) {
    val enterExpand = remember { expandVertically(animationSpec = tween(EXPAND_ANIMATION_DURATION)) }
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = FADE_IN_ANIMATION_DURATION,
                easing = FastOutLinearInEasing
            )
        )
    }
    val exitCollapse = remember { shrinkVertically(animationSpec = tween(COLLAPSE_ANIMATION_DURATION)) }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = FADE_OUT_ANIMATION_DURATION,
                easing = LinearOutSlowInEasing
            )
        )
    }
    AnimatedVisibility(
        visible = isVisible,
        initiallyVisible = initialVisibility,
        enter = enterExpand + enterFadeIn,
        exit = exitCollapse + exitFadeOut
    ) {
        content()
    }
}