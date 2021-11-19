package com.example.trailz.ui.onboarding

import android.animation.ValueAnimator
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trailz.R
import com.example.trailz.ui.common.compose.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun Onboarding(
    finishOnboarding: () -> Unit
) {
    val onboardingList = listOf(
        Triple(
            stringResource(R.string.onboarding_problem_title),
            stringResource(R.string.onboarding_problem_description),
            "problem.json"
        ),
        Triple(
            stringResource(R.string.onboarding_opportunity_title),
            stringResource(R.string.onboarding_opportunity_description),
            "target.json"
        ),
        Triple(
            stringResource(R.string.onboarding_solution_title),
            stringResource(R.string.onboarding_solution_description),
            "trail.json"
        )
    )

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = onboardingList.count(),
        initialOffscreenLimit = 2
    )

    Onboarding(
        pagerState = pagerState,
        onboardingList = onboardingList,
        finishOnboarding = finishOnboarding,
    )
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
internal fun Onboarding(
    pagerState: PagerState,
    onboardingList: List<Triple<String, String, String>>,
    finishOnboarding: () -> Unit
) {

    val enterExpand = remember { expandVertically(animationSpec = tween(500)) }
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = 500,
                easing = FastOutLinearInEasing
            )
        )
    }
    val exitCollapse = remember { shrinkVertically(animationSpec = tween(400)) }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = 400,
                easing = LinearOutSlowInEasing
            )
        )
    }

    val onLastPage = pagerState.currentPage == onboardingList.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
        ) { page ->
            PagerItem(
                item = onboardingList[page],
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        HorizontalPagerIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            pagerState = pagerState,
        )

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visible = onLastPage,
            enter = enterExpand + enterFadeIn,
            exit = exitCollapse + exitFadeOut
        ) {
            Button(
                onClick = finishOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.onboarding_cta))
            }
        }
    }
}

@Composable
internal fun PagerItem(
    modifier: Modifier = Modifier,
    item: Triple<String, String, String>
) {
    val (title, description, animationFile) = item
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieView(
            context = LocalContext.current,
            animationFile = animationFile,
            repeatFor = ValueAnimator.INFINITE,
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
