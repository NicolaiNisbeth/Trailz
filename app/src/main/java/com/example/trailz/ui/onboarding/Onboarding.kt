package com.example.trailz.ui.onboarding

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trailz.ui.common.compose.LottieView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun Onboarding(
    finishOnboarding: () -> Unit
){
    val onboardingList = listOf(
        Triple(
            "Team Collaborations",
            "Our tools help your teams collaborate for the best output results",
            "rocket.json"
        ),
        Triple(
            "Improve Productivity",
            "Our tools are designed to improve productivity by automating all the stuff for you",
            "covid.json"
        ),
        Triple(
            "Growth Tracking",
            "We provide dashboard and charts to track your growth easily and suggestions.",
            "delivery.json"
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
        openNextPage = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween()
                )
            }
        }
    )
}

@ExperimentalPagerApi
@Composable
internal fun Onboarding(
    pagerState: PagerState,
    onboardingList: List<Triple<String, String, String>>,
    openNextPage: () -> Unit,
    finishOnboarding: () -> Unit
){

    val onLastPage = pagerState.currentPage == onboardingList.lastIndex

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                PagerItem(
                    item = onboardingList[page],
                    modifier = Modifier
                )
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
            )

            Button(
                onClick = { if (!onLastPage) openNextPage() else finishOnboarding() },
                modifier = Modifier
                    .animateContentSize(tween())
                    .clip(CircleShape)
            ) {
                Text(
                    text = if (onLastPage) "Let's Begin" else "Next",
                )
            }
        }
    }
}

@Composable
internal fun PagerItem(
    modifier: Modifier,
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
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
