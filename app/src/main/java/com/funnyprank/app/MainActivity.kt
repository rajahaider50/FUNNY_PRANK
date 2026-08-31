package com.funnyprank.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.funnyprank.app.ui.screens.ComingSoonScreen
import com.funnyprank.app.ui.screens.IntroScreen
import com.funnyprank.app.ui.screens.SplashScreen
import com.funnyprank.app.ui.theme.FunnyPrankTheme

enum class AppStage { SPLASH, INTRO, COMING_SOON }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FunnyPrankTheme {
                var stage by remember { mutableStateOf(AppStage.SPLASH) }
                when (stage) {
                    AppStage.SPLASH -> SplashScreen(onFinished = { stage = AppStage.INTRO })
                    AppStage.INTRO -> IntroScreen(onIntroDone = { stage = AppStage.COMING_SOON })
                    AppStage.COMING_SOON -> ComingSoonScreen(onBackToIntro = { stage = AppStage.INTRO })
                }
            }
        }
    }
}
