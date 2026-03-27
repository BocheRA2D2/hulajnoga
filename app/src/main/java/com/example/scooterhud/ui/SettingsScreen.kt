package com.example.scooterhud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scooterhud.viewmodel.HudUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: HudUiState,
    onToggleAutoPause: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleOrientation: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            SettingsItem(
                label = "Autopauza",
                description = "Zatrzymuj timer przy braku ruchu",
                checked = uiState.isAutoPauseEnabled,
                onCheckedChange = { onToggleAutoPause() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingsItem(
                label = "Ciemny motyw",
                description = "Zalecany do jazdy nocą",
                checked = uiState.isDarkTheme,
                onCheckedChange = { onToggleTheme() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                label = "Układ pionowy",
                description = "Włącz dla uchwytów pionowych",
                checked = uiState.isPortrait,
                onCheckedChange = { onToggleOrientation() }
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "ScooterHUD v1.0",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingsItem(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
