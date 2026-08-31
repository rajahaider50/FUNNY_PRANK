package com.funnyprank.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.R
import com.funnyprank.app.ui.components.PrankBackdrop
import com.funnyprank.app.ui.components.PrankGlassCard
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.GlassBorder
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextMutedDark
import com.funnyprank.app.ui.theme.TextWhite

/**
 * Placeholder shown after onboarding — "Coming Soon" glass card,
 * pointing to future features. Returns to Intro 1 via BACK TO INTRO.
 */
@Composable
fun ComingSoonScreen(onBackToIntro: () -> Unit) {
    PrankBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // decorative ambient dots
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BrandRed)
                )
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BrandGreen)
                )
            }

            Spacer(Modifier.height(28.dp))

            PrankGlassCard(
                cornerRadius = 34.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // glowing icon circle
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(BrandRed.copy(alpha = 0.25f), BrandGreen.copy(alpha = 0.18f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bolt_white),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(Modifier.height(26.dp))

                    Text(
                        text = "COMING SOON",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        color = TextWhite
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "More Features Are On The Way",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "NEXT FEATURE  •  IN DEVELOPMENT",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.6.sp,
                        color = TextMutedDark,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(34.dp))

            // Back to intro
            Box(
                Modifier
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(BrandRed, BrandGreen)))
                    .clickable { onBackToIntro() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BACK TO INTRO",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "VERSION 1.0.0",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                color = TextMutedDark.copy(alpha = 0.7f)
            )
        }
    }
}
