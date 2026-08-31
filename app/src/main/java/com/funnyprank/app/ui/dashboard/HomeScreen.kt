package com.funnyprank.app.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.service.FloatingOverlayService
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextGreenSoft
import com.funnyprank.app.ui.theme.TextWhite

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

    fun enable() {
        if (Settings.canDrawOverlays(context)) {
            viewModel.setFloatingControl(true)
            FloatingOverlayService.start(context)
            active = true
            viewModel.toast("Launch activated")
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    fun disable() {
        viewModel.setFloatingControl(false)
        FloatingOverlayService.stop(context)
        active = false
        viewModel.toast("Launch disabled")
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

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            LaunchRing()
        }

        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
            LaunchToggle(active = active, onToggle = { if (active) disable() else enable() })
            Spacer(Modifier.height(9.dp))
            Text(
                text = if (active) "Floating audio control is active." else "Launch enables the floating audio control.",
                color = TextGray, fontSize = 10.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LaunchToggle(active: Boolean, onToggle: () -> Unit) {
    val fraction by animateFloatAsState(if (active) 1f else 0f, tween(450))
    val knobSize = 56.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            .padding(6.dp)
    ) {
        val travel = maxWidth - knobSize
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (active) "ACTIVE — TAP TO DISABLE" else "TAP TO LAUNCH",
                    color = if (active) TextGreenSoft else TextGray,
                    fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp
                )
                if (active) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Rounded.Bolt, null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                }
            }
        }
        Box(
            modifier = Modifier
                .offset(x = travel * fraction)
                .size(knobSize)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (active)
                        Brush.linearGradient(listOf(BrandGreen, Color(0xFF0EB070)))
                    else androidx.compose.ui.graphics.SolidColor(Color(0xFF101317))
                )
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                .clickable(onClick = onToggle),
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

@Composable
private fun LaunchRing() {
    val ringSize = remember { 265.dp }
    Box(modifier = Modifier.size(ringSize)) {
        Canvas(Modifier.matchParentSize()) {
            val radius = size.minDimension / 2f
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
                .background(Color(0xFF07090C))
                .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
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
                    Icon(Icons.Rounded.Bolt, null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text("Launch", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(7.dp))
                Text("READY TO START", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
            }
        }
    }
}
