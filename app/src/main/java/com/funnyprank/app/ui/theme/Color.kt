package com.funnyprank.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

// =====================================================
//  FUNNY PRANK — LOCKED DESIGN SYSTEM (BLACK + RED + GREEN)
//  Master reference: /1 website/text.txt (Design Lock)
// =====================================================

// ---- Background ----
val BgBlack = Color(0xFF050608)
val BgBlackSoft = Color(0xFF080A0D)
val BgBlackDeep = Color(0xFF040507)

// ---- Brand red ----
val BrandRed = Color(0xFFFF3150)
val BrandRedDeep = Color(0xFFD7193B)
val RedGlow = Color(0x2BFF3150)         // rgba(255,49,80,0.17)

// ---- Brand green ----
val BrandGreen = Color(0xFF18E58B)
val BrandGreenDeep = Color(0xFF0CAF69)
val GreenGlow = Color(0x2118E58B)       // rgba(24,229,139,0.13)

// ---- Text ----
val TextWhite = Color(0xFFF8FAFC)
val TextGray = Color(0xFF89939D)
val TextMutedDark = Color(0xFF454D55)
val TextGreenSoft = Color(0xFF7BE9B6)

// ---- Glass ----
val GlassBorder = Color(0x1BFFFFFF)     // rgba(255,255,255,0.105)
val GlassHi = Color(0x13FFFFFF)         // rgba(255,255,255,0.075)
val GlassLow = Color(0x06FFFFFF)        // rgba(255,255,255,0.025)

// =====================================================
//  Background brush
// =====================================================
val AppBackgroundBrush = Brush.linearGradient(
    colors = listOf(BgBlackDeep, BgBlackSoft, BgBlack),
    start = Offset(0f, 0f),
    end = Offset(1400f, 2400f)
)
