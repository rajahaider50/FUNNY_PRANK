package com.funnyprank.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funnyprank.app.data.db.AppSettingsEntity
import com.funnyprank.app.ui.MainViewModel
import com.funnyprank.app.ui.screens.PermissionHost
import com.funnyprank.app.ui.screens.MainDashboard
import com.funnyprank.app.ui.theme.FunnyPrankTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FunnyPrankTheme {
                val vm: MainViewModel = viewModel()
                val settings by vm.settings.collectAsState()
                if (!settings.onboardingDone) {
                    PermissionHost(vm = vm)
                } else {
                    MainDashboard(vm = vm, settings = settings)
                }
            }
        }
    }
}
