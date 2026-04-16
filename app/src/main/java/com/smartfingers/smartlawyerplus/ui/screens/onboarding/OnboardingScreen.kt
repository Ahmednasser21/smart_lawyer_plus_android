@file:JvmName("OnboardingPageKt")

package com.smartfingers.smartlawyerplus.ui.screens.onboarding

import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.ui.theme.AppGray
import com.smartfingers.smartlawyerplus.ui.theme.Divider
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.TextOnPrimary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch



@Composable
fun OnboardingScreen(
    onSkip: () -> Unit,
    onFinish: () -> Unit,
) {
    val pages = listOf(
        OnboardingPage(
            titleRes = R.string.onboarding_one_title,
            descriptionRes = R.string.onboarding_one_description,
            imageRes = R.drawable.judge_pana,
        ),
        OnboardingPage(
            titleRes = R.string.onboarding_one_title,
            descriptionRes = R.string.onboarding_one_description,
            imageRes = R.drawable.judge_rafiki,
        ),
        OnboardingPage(
            titleRes = R.string.onboarding_one_title,
            descriptionRes = R.string.onboarding_one_description,
            imageRes = R.drawable.judge,
        ),
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val currentPage = pagerState.currentPage
    val isLastPage = currentPage == pages.lastIndex
    val isPreLastPage = currentPage == pages.lastIndex - 1

    val fabOffsetFraction by animateFloatAsState(
        targetValue = if (isLastPage) 0.5f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "fab_offset",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 112.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            pages.forEachIndexed { index, _ ->
                val isSelected = currentPage == index
                val dotWidth by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "dot_width_$index",
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(if (isSelected) Primary else Divider),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
        ) {
            AnimatedVisibility(
                visible = !isLastPage && !isPreLastPage,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300)),
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppGray.copy(alpha = 0.15f)),
                ) {
                    Text(
                        text = stringResource(R.string.skip),
                        color = AppGray,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(currentPage + 1)
                        }
                    }
                },
                containerColor = Primary,
                contentColor = TextOnPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = (-fabOffsetFraction * 300).dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = if (isLastPage) "Finish" else "Next",
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(page.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(page.descriptionRes),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
        )
    }
}