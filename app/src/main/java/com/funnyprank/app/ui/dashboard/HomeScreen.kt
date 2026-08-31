package com.funnyprank.app.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.service.FloatingOverlayService
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextGreenSoft
import com.funnyprank.app.ui.theme.TextWhite
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: DashboardViewModel, onOpenSettings: () -> Unit) {
    val context = LocalContext.current

    var active by remember { mutableStateOf(Settings.canDrawOverlays(context) && viewModel.settings.floatingControl) }

    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(context)) {
            viewModel.setFloatingControl(true)
            FloatingOverlayService.start(context)
            active = true
        }
    }

    fun ensureOverlayPermission(onGranted: () -> Unit) {
        if (Settings.canDrawOverlays(context)) {
            viewModel.setFloatingControl(true)
            FloatingOverlayService.start(context)
            active = true
            onGranted()
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    fun enable() {
        if (Settings.canDrawOverlays(context)) {
            viewModel.setFloatingControl(true)
            FloatingOverlayService.start(context)
            active = true
            viewModel.toast("Floating audio control enabled")
        }
    }

    fun disable() {
        viewModel.setFloatingControl(false)
        FloatingOverlayService.stop(context)
        active = false
        viewModel.toast("Floating audio control disabled")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandLogo(40)
                Spacer(Modifier.width(10.dp))
                Text("FUNNY ", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("PRANK", color = BrandGreen, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color.White.copy(alpha = 0.055f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(13.dp))
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Settings, null, tint = Color(0xFFDCE2E6), modifier = Modifier.size(20.dp))
            }
        }

        LaunchSection(
            active = active,
            enabled = Settings.canDrawOverlays(context),
            modifier = Modifier.weight(1f),
            onEnable = { enable() }
        )

        SlideToLaunch(
            active = active,
            onActivate = { enable() },
            onDeactivate = { disable() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        )
        Spacer(Modifier.height(9.dp))
        Text(
            text = if (active)
                "Floating audio control is active — it stays above other apps."
            else
                "Slide the power button to enable the floating audio control.",
            color = TextGray, fontSize = 10.sp, textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(8.dp))
    }
}

/**
 * Centered, responsive launch ring. Sized against the available content
 * box so it never spills off small screens and stays horizontally centered.
 */
@Composable
private fun LaunchSection(active: Boolean, enabled: Boolean, modifier: Modifier = Modifier, onEnable: () -> Unit) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val ringSize: Dp = minOf(maxWidth * 0.68f, maxHeight * 0.96f)
        LaunchRing(active = active, enabled = enabled, size = ringSize, onClick = onEnable)
    }
}

@Composable
private fun LaunchRing(active: Boolean, enabled: Boolean, size: Dp, onClick: () -> Unit) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val radius = this.size.minDimension / 2f
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(BrandRed, Color(0xFFFF9C39), Color(0xFFFFE600), BrandGreen, BrandRed)
                ),
                radius = radius,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp)
                .clip(CircleShape)
                .background(if (active) Color(0xFF08130D) else Color(0xFF07090C))
                .border(1.dp, if (active) BrandGreen.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f), CircleShape)
                .clickable(enabled = enabled) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(BrandRed.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(17.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Bolt, null, tint = if (active) BrandGreen else BrandGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    if (active) "Active" else "Launch",
                    color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    if (active) "AUDIO CONTROL ACTIVE" else "READY TO START",
                    color = if (active) TextGreenSoft else TextGray, fontSize = 9.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Slide-to-launch control: press, hold and drag the power knob. Crossing
 * ~72% of the track toggles the state; otherwise the knob springs back.
 */
@Composable
private fun SlideToLaunch(
    active: Boolean,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val fraction = remember { Animatable(if (active) 1f else 0f) }
    var knob by remember { mutableStateOf(if (active) 1f else 0f) }
    var dragging by remember { mutableStateOf(false) }

    LaunchedEffect(active) {
        if (!dragging) {
            fraction.animateTo(if (active) 1f else 0f, tween(350))
            knob = fraction.value
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            .padding(6.dp)
    ) {
        val knobSize = 54.dp
        val trackDp = maxWidth - knobSize
        val trackPx = with(density) { trackDp.toPx() }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(active, trackPx) {
                    detectHorizontalDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = {
                            dragging = false
                            val f = fraction.value
                            when {
                                !active && f >= 0.72f -> {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onActivate()
                                }
                                active && f <= 0.28f -> {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onDeactivate()
                                }
                                else -> scope.launch {
                                    fraction.animateTo(if (active) 1f else 0f, tween(220))
                                    knob = fraction.value
                                }
                            }
                        },
                        onDragCancel = {
                            dragging = false
                            scope.launch {
                                fraction.animateTo(if (active) 1f else 0f, tween(220))
                                knob = fraction.value
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val delta = dragAmount / trackPx
                            val next = if (active) fraction.value - delta else fraction.value + delta
                            val c = next.coerceIn(0f, 1f)
                            knob = c
                            scope.launch { fraction.snapTo(c) }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (active) "SLIDE TO DISABLE" else "SLIDE TO LAUNCH",
                color = if (active) TextGreenSoft else TextGray,
                fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp
            )
        }

        Box(
            modifier = Modifier
                .offset(x = trackDp * knob)
                .size(knobSize)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (active)
                        Brush.linearGradient(listOf(BrandGreen, Color(0xFF0EB070)))
                    else SolidColor(Color(0xFF101317))
                )
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.PowerSettingsNew,
                null,
                tint = if (active) Color(0xFF03140D) else Color.White,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
