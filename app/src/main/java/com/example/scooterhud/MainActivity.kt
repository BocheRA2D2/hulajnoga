package com.example.scooterhud

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scooterhud.ui.HudScreen
import com.example.scooterhud.ui.SettingsScreen
import com.example.scooterhud.ui.theme.ScooterHudTheme
import com.example.scooterhud.viewmodel.HudViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HudViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.startLocationUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Zawsze włączony ekran
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val navController = rememberNavController()

            // Dynamiczna zmiana orientacji
            requestedOrientation = if (uiState.isPortrait) {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            ScooterHudTheme(darkTheme = uiState.isDarkTheme) {
                NavHost(navController = navController, startDestination = "hud") {
                    composable("hud") {
                        HudScreen(
                            uiState = uiState,
                            onStartStop = { viewModel.onStartStop() },
                            onOpenSettings = { navController.navigate("settings") },
                            onRefreshWeather = { viewModel.refreshWeather() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            uiState = uiState,
                            onToggleAutoPause = { viewModel.toggleAutoPause() },
                            onToggleTheme = { viewModel.toggleTheme() },
                            onToggleOrientation = { viewModel.toggleOrientation() },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopLocationUpdates()
    }
}
