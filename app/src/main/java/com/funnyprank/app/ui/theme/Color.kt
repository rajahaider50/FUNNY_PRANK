package com.funnyprank.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---- Luxury dark glass background gradient ----
val DeepNavy = Color(0xFF0B1026)
val MidNavy = Color(0xFF111A3A)
val AccentTop = Color(0xFF6A2CFF)
val AccentMid = Color(0xFF00C2FF)
val AccentPink = Color(0xFFFF3D8E)
val AccentGold = Color(0xFFFFC53D)

// ---- Glass surfaces (translucent whites) ----
val GlassWhite = Color(0x33FFFFFF)
val GlassWhiteStrong = Color(0x55FFFFFF)
val GlassBorder = Color(0x59FFFFFF)
val GlassBorderSoft = Color(0x2EFFFFFF)

// ---- Text on dark ----
val TextPrimary = Color(0xFFF4F6FF)
val TextSecondary = Color(0xFFB9C2E0)
val TextMuted = Color(0xFF7D86AC)

// ---- Semantic ----
val Success = Color(0xFF2BD47C)
val Warning = Color(0xFFFFC53D)
val Danger = Color(0xFFFF4D6D)

// ---- Chat bubble blues / glows ----
val GlowBlue = Color(0xFF0055FF)
val GlowCyan = Color(0xFF00E5FF)
val GlowPurple = Color(0xFF7C4DFF)
val GlowPink = Color(0xFFFF4081)

// ---- Brand gradient for the lightning launch ----
val LaunchGradient = Brush.linearGradient(
    listOf(AccentMid, AccentPurple, AccentPink)
)
val AccentPurple = Color(0xFF7C4DFF)

val GlassBackgroundBrush = Brush.verticalGradient(
    listOf(DeepNavy, MidNavy)
)
