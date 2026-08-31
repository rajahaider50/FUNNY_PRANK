package com.funnyprank.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FilePresent
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.funnyprank.app.R
import com.funnyprank.app.permissions.PermissionManager
import com.funnyprank.app.ui.components.PrankBackdrop
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.GlassBorder
import com.funnyprank.app.ui.theme.GlassHi
import com.funnyprank.app.ui.theme.GlassLow
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextGreenSoft
import com.funnyprank.app.ui.theme.TextWhite

private data class PermissionCardUi(
    val card: PermissionManager.Card,
    val name: String,
    val desc: String,
    val icon: ImageVector
)

private val cards = listOf(
    PermissionCardUi(PermissionManager.Card.MICROPHONE, "Microphone",
        "Required for microphone and voice communication.", Icons.Rounded.Mic),
    PermissionCardUi(PermissionManager.Card.AUDIO_MEDIA, "Audio & Media",
        "Allow access to audio files imported into the app.", Icons.Rounded.FilePresent),
    PermissionCardUi(PermissionManager.Card.NEARBY_DEVICES, "Nearby Devices",
        "Required for Bluetooth audio device compatibility.", Icons.Rounded.Bluetooth),
    PermissionCardUi(PermissionManager.Card.NOTIFICATIONS, "Notifications",
        "Keep important playback and app status notifications.", Icons.Rounded.Notifications)
)

@Composable
fun PermissionsScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Re-evaluate real permission state on every resume.
    var satisfied by remember {
        mutableStateOf(PermissionManager.allCards().map { it to PermissionManager.isSatisfied(context, it) })
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                satisfied = PermissionManager.allCards().map {
                    it to PermissionManager.isSatisfied(context, it)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val allDone = satisfied.all { it.second }
    val doneCount = satisfied.count { it.second }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        satisfied = PermissionManager.allCards().map {
            it to PermissionManager.isSatisfied(context, it)
        }
    }

    fun requestCard(card: PermissionManager.Card) {
        val perms = PermissionManager.requiredPermissions(card)
        if (perms.isEmpty()) {
            satisfied = PermissionManager.allCards().map { it to PermissionManager.isSatisfied(context, it) }
            return
        }
        permissionLauncher.launch(perms)
    }

    PrankBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Header()
            Spacer(Modifier.height(18.dp))

            ProgressArea(count = doneCount, total = cards.size)

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                itemsIndexed(cards) { _, item ->
                    val isDone = satisfied.firstOrNull { it.first == item.card }?.second == true
                    PermissionCard(
                        ui = item,
                        done = isDone,
                        enabled = !isDone,
                        onClick = { requestCard(item.card) }
                    )
                }
                item { Spacer(Modifier.height(6.dp)) }
            }

            Spacer(Modifier.height(4.dp))

            Footer(
                allDone = allDone,
                onContinue = onContinue
            )
        }
    }
}

@Composable
private fun Header() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Funny Prank logo",
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(25.dp))
                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(25.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(13.dp))
        Text(
            text = "Quick Setup",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp,
            color = TextWhite
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Allow the required permissions to unlock\nthe complete Funny Prank audio experience.",
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProgressArea(count: Int, total: Int) {
    val fraction by animateFloatAsState(
        targetValue = if (total == 0) 0f else count.toFloat() / total,
        animationSpec = tween(500)
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Setup Progress", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp, color = Color(0xFF737C84))
            Text("$count / $total", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = TextGreenSoft)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.07f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(BrandRed, BrandGreen)))
            )
        }
    }
}

@Composable
private fun PermissionCard(ui: PermissionCardUi, done: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(21.dp)
    val accent = if (done) BrandGreen else BrandRed
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        if (done) accent.copy(alpha = 0.10f) else GlassHi,
                        GlassLow
                    )
                )
            )
            .border(1.dp, if (done) accent.copy(alpha = 0.35f) else GlassBorder, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(49.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background((if (done) BrandGreen else BrandRed).copy(alpha = 0.09f))
                    .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(ui.icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(11.dp))
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(ui.name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(3.dp))
                Text(ui.desc, color = Color(0xFF707981), fontSize = 10.sp, lineHeight = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.width(10.dp))
            // Action / check
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (done)
                            Brush.linearGradient(listOf(Color(0xFF25EE94), Color(0xFF0FC878)))
                        else Brush.solidColor(Color.White.copy(alpha = 0.06f))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (done) {
                    Icon(Icons.Rounded.Check, contentDescription = "Granted", tint = Color(0xFF03130B), modifier = Modifier.size(16.dp))
                } else {
                    Text("Allow", color = Color(0xFFDCE1E5), fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun Footer(allDone: Boolean, onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Icon(
                if (allDone) Icons.Rounded.Check else Icons.Rounded.Lock,
                contentDescription = null,
                tint = if (allDone) BrandGreen else BrandRed,
                modifier = Modifier.size(10.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (allDone) "All permissions completed" else "Complete all permissions to continue",
                color = if (allDone) Color(0xFF78D9AD) else Color(0xFF656E76),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        val enabledShape = RoundedCornerShape(22.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(enabledShape)
                .background(
                    if (allDone)
                        Brush.linearGradient(listOf(Color(0xFF24ED91), Color(0xFF0FC979)))
                    else Brush.solidColor(Color.White.copy(alpha = 0.055f))
                )
                .border(1.dp, if (allDone) BrandGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.07f), enabledShape)
                .clickable(enabled = allDone, onClick = onContinue),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Continue",
                    color = if (allDone) Color(0xFF03130B) else Color(0xFF687179),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = if (allDone) Color(0xFF03130B) else Color(0xFF687179),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
