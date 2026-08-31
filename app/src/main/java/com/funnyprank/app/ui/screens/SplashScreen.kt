package com.funnyprank.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.R
import com.funnyprank.app.ui.components.PrankBackdrop
import com.funnyprank.app.ui.components.PrankGlassCard
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextMutedDark
import com.funnyprank.app.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Premium Splash Screen — dark glass, red/green ambient, logo in a glass
 * frame, animated loading bar. Auto-advances to the intro onboarding.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            progress.animateTo(0.32f, tween(950, easing = LinearEasing))
            progress.animateTo(0.73f, tween(1000, easing = LinearEasing))
            progress.animateTo(1f, tween(750, easing = LinearEasing))
        }
        delay(2700)
        onFinished()
    }

    PrankBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PrankGlassCard(
                cornerRadius = 31.dp,
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .aspectRatio(1f)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Funny Prank logo",
                        modifier = Modifier
                            .fillMaxWidth(0.86f)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextWhite)) { append("FUNNY ") }
                    withStyle(
                        SpanStyle(
                            brush = Brush.linearGradient(listOf(BrandRed, Color(0xFFFF536C), BrandGreen))
                        )
                    ) { append("PRANK") }
                },
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.4).sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "PREMIUM FUNNY AUDIO SOUNDBOARD",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.3.sp,
                color = TextGray
            )

            Spacer(Modifier.height(52.dp))

            // Loading area
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PulsingDot()
                    Text(
                        text = "INITIALIZING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.3.sp,
                        color = TextMutedDark
                    )
                }
                Spacer(Modifier.height(12.dp))
                // Progress track (fixed 220dp)
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value.coerceIn(0f, 1f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Brush.horizontalGradient(listOf(BrandRed, BrandGreen)))
                    )
                }
            }

            Spacer(Modifier.height(72.dp))

            // Footer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bolt_white),
                        contentDescription = null,
                        tint = BrandRed,
                        modifier = Modifier.height(8.dp).width(8.dp)
                    )
                    Text(
                        text = "LOCAL  ·  FAST  ·  PRIVATE",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.2.sp,
                        color = TextMutedDark
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "VERSION 1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = TextMutedDark.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun PulsingDot() {
    val transition = rememberInfiniteTransition(label = "dot")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        Modifier
            .height(7.dp)
            .width(7.dp)
            .clip(RoundedCornerShape(50))
            .background(BrandGreen.copy(alpha = alpha))
    )
}
