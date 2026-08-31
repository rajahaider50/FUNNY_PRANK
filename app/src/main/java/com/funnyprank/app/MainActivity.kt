package com.funnyprank.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.funnyprank.app.permissions.PermissionManager
import com.funnyprank.app.ui.dashboard.DashboardHost
import com.funnyprank.app.ui.screens.IntroScreen
import com.funnyprank.app.ui.screens.PermissionsScreen
import com.funnyprank.app.ui.screens.SplashScreen
import com.funnyprank.app.ui.theme.FunnyPrankTheme

enum class AppStage { SPLASH, INTRO, PERMISSIONS, DASHBOARD }

/**
 * Dynamic routing:
 *  - First install:  Splash -> Intro -> Permissions -> Dashboard
 *  - Returning user: Splash -> Dashboard (when permissions still granted)
 *  - Permission revoked: Splash -> Permissions (recovery) -> Dashboard
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = AppContainer.get(this)
        val settings = container.settings

        setContent {
            FunnyPrankTheme {
                var stage by remember { mutableStateOf(AppStage.SPLASH) }

                // Decide where the splash should land, using persistent state +
                // the REAL current Android permission status.
                fun routeAfterSplash() {
                    val onboarded = settings.onboardingCompleted
                    val permsOk = PermissionManager.allSatisfied(this)
                    stage = when {
                        onboarded && permsOk -> AppStage.DASHBOARD
                        onboarded -> AppStage.PERMISSIONS   // recovery: some permission was revoked
                        else -> AppStage.INTRO              // first run
                    }
                }

                when (stage) {
                    AppStage.SPLASH -> SplashScreen(onFinished = { routeAfterSplash() })
                    AppStage.INTRO -> IntroScreen(onIntroDone = { stage = AppStage.PERMISSIONS })
                    AppStage.PERMISSIONS -> PermissionsScreen(onContinue = {
                        settings.onboardingCompleted = true
                        routeAfterSplash()
                    })
                    AppStage.DASHBOARD -> DashboardHost()
                }
            }
        }
    }
}
