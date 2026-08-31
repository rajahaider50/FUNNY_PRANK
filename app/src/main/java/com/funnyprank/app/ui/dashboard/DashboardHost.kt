package com.funnyprank.app.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.funnyprank.app.ui.components.PrankBackdrop
import com.funnyprank.app.ui.dashboard.DashboardViewModel.DashboardTab
import com.funnyprank.app.ui.theme.BrandGreen

private data class NavItem(val tab: DashboardTab, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(DashboardTab.HOME, Icons.Rounded.Home, "Home"),
    NavItem(DashboardTab.AUDIO, Icons.Rounded.AudioFile, "Audio"),
    NavItem(DashboardTab.UPLOAD, Icons.Rounded.CloudUpload, "Upload"),
    NavItem(DashboardTab.EDIT, Icons.Rounded.Edit, "Edit"),
    NavItem(DashboardTab.SETTINGS, Icons.Rounded.Settings, "Setting")
)

@Composable
fun DashboardHost(hostVm: DashboardViewModel = viewModel()) {
    val activeTab by hostVm.activeTab.collectAsState()
    val message by hostVm.message.collectAsState()

    PrankBackdrop {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                when (activeTab) {
                    DashboardTab.HOME -> HomeScreen(hostVm, onOpenSettings = { hostVm.setTab(DashboardTab.SETTINGS) })
                    DashboardTab.AUDIO -> AudioScreen(hostVm)
                    DashboardTab.UPLOAD -> UploadScreen(hostVm, onOpenLibrary = { hostVm.setTab(DashboardTab.AUDIO) })
                    DashboardTab.EDIT -> EditScreen(hostVm)
                    DashboardTab.SETTINGS -> SettingsScreen(hostVm)
                }
            }

            BottomNavBar(
                activeTab = activeTab,
                onSelect = { hostVm.setTab(it) }
            )
        }

        ToastHost(message = message) { hostVm.consumeMessage() }
    }
}

@Composable
private fun BottomNavBar(activeTab: DashboardTab, onSelect: (DashboardTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 9.dp, vertical = 8.dp)
            .background(Color(0xE60A0C0F), RoundedCornerShape(23.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(23.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { item ->
            val selected = item.tab == activeTab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(17.dp))
                    .background(if (selected) BrandGreen.copy(alpha = 0.10f) else Color.Transparent)
                    .clickable { onSelect(item.tab) }
                    .padding(vertical = 6.dp)
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = if (selected) BrandGreen else Color(0xFF69727A),
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    item.label,
                    color = if (selected) BrandGreen else Color(0xFF69727A),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ToastHost(message: String?, onDismiss: () -> Unit) {
    val visible = message != null
    LaunchedEffect(message) {
        if (message != null) {
            kotlinx.coroutines.delay(2200)
            onDismiss()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = visible) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 86.dp)
                    .background(Color(0xFF101419), RoundedCornerShape(13.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(13.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(message.orEmpty(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
