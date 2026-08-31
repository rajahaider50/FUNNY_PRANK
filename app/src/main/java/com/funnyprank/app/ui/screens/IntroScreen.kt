package com.funnyprank.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.R
import com.funnyprank.app.ui.components.PrankBackdrop
import com.funnyprank.app.ui.components.PrankGlassCard
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.BrandRedDeep
import com.funnyprank.app.ui.theme.GlassBorder
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextGreenSoft
import com.funnyprank.app.ui.theme.TextWhite
import kotlinx.coroutines.launch

/**
 * FOUR onboarding intro pages — swipe + Next + Skip + dots.
 *
 * Page order (locked by spec):
 *  1. WELCOME          — Your Funny Soundboard
 *  2. YOUR LIBRARY     — Import Your Audio
 *  3. INSTANT PLAYBACK — Play Anytime
 *  4. READY            — Built For Fun (GET STARTED)
 */
private data class IntroPage(
    val image: Int,
    val eyebrow: String,
    val eyebrowIcon: ImageVector,
    val prefix: String,
    val accentWord: String,
    val accentColor: Color,
    val description: String
)

private val introPages = listOf(
    IntroPage(
        image = R.drawable.intro1,
        eyebrow = "WELCOME",
        eyebrowIcon = Icons.Rounded.Bolt,
        prefix = "Your ",
        accentWord = "Funny",
        accentColor = BrandRed,
        description = "Keep your favorite funny sounds, memes and prank audio together in one fast and beautiful place."
    ),
    IntroPage(
        image = R.drawable.intro2,
        eyebrow = "YOUR LIBRARY",
        eyebrowIcon = Icons.Rounded.FolderOpen,
        prefix = "Import ",
        accentWord = "Your Audio",
        accentColor = BrandGreen,
        description = "Import your own audio files and organized sound collections directly from your device storage."
    ),
    IntroPage(
        image = R.drawable.intro3,
        eyebrow = "INSTANT PLAYBACK",
        eyebrowIcon = Icons.Rounded.Headphones,
        prefix = "Play ",
        accentWord = "Anytime",
        accentColor = BrandRed,
        description = "Find a sound, tap play and enjoy instant playback with a clean, responsive audio experience."
    ),
    IntroPage(
        image = R.drawable.intro4,
        eyebrow = "READY",
        eyebrowIcon = Icons.Rounded.AutoAwesome,
        prefix = "Built For ",
        accentWord = "Fun",
        accentColor = BrandGreen,
        description = "One final step and your local soundboard will be ready to use. Let's set everything up."
    )
)

@Composable
fun IntroScreen(onIntroDone: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    val scope = rememberCoroutineScope()

    fun advance() {
        scope.launch {
            if (pagerState.currentPage < introPages.size - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            } else {
                onIntroDone()
            }
        }
    }

    PrankBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar — Skip (top-right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.045f))
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .clickable { onIntroDone() }
                        .padding(horizontal = 16.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = "SKIP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TextGray
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                IntroPageContent(page = introPages[page])
            }

            // Bottom navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp, top = 6.dp, bottom = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(introPages.size) { i ->
                        PageDot(active = pagerState.currentPage == i) {
                            scope.launch { pagerState.animateScrollToPage(i) }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                val lastPage = pagerState.currentPage >= introPages.size - 1
                if (lastPage) {
                    Box(
                        Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF22ED91), Color(0xFF0FC978))))
                            .clickable { onIntroDone() }
                            .padding(horizontal = 22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GET STARTED",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color(0xFF03130B)
                        )
                    }
                } else {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(BrandRed, BrandRedDeep)))
                            .clickable { advance() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.width(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IntroPageContent(page: IntroPage) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
    ) {
        val maxW = maxWidth
        val maxH = maxHeight
        // Reserve a generous budget for eyebrow + title + description + spacing.
        val contentBudget = maxH * 0.34f
        // Card size: fit within width, and leave room for the text below.
        val cardSize = minOf(maxW, contentBudget)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glass image card
            PrankGlassCard(
                cornerRadius = 34.dp,
                modifier = Modifier
                    .width(cardSize)
                    .aspectRatio(1f)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(page.image),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // Eyebrow pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(BrandGreen.copy(alpha = 0.045f))
                    .border(1.dp, BrandGreen.copy(alpha = 0.15f), RoundedCornerShape(100))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = page.eyebrowIcon,
                    contentDescription = null,
                    tint = TextGreenSoft,
                    modifier = Modifier.width(12.dp).height(12.dp)
                )
                Text(
                    text = page.eyebrow,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = TextGreenSoft
                )
            }

            Spacer(Modifier.height(16.dp))

            // Title with accent
            val annotated = buildAnnotatedString {
                withStyle(SpanStyle(color = TextWhite)) { append(page.prefix) }
                withStyle(SpanStyle(color = page.accentColor)) { append(page.accentWord) }
            }
            Text(
                text = annotated,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth(0.92f)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PageDot(active: Boolean, onClick: () -> Unit) {
    val targetWidth = if (active) 26.dp else 7.dp
    val animatedWidth by animateFloatAsState(
        targetValue = targetWidth.value,
        animationSpec = tween(220),
        label = "dotWidth"
    )
    Box(
        modifier = Modifier
            .width(animatedWidth.dp)
            .height(7.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (active) {
                    Brush.linearGradient(listOf(BrandRed, BrandGreen))
                } else {
                    SolidColor(Color(0xFF333A41))
                }
            )
            .clickable { onClick() }
    )
}
