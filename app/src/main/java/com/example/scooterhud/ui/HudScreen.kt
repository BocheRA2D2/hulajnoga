package com.example.scooterhud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scooterhud.data.model.WeatherInfo
import com.example.scooterhud.viewmodel.HudUiState
import com.example.scooterhud.viewmodel.RideState

@Composable
fun HudScreen(
    uiState: HudUiState,
    onStartStop: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefreshWeather: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Ustawienia",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.size(26.dp)
            )
        }

        if (uiState.isPortrait) {
            PortraitHud(uiState, onStartStop, onRefreshWeather)
        } else {
            LandscapeHud(uiState, onStartStop, onRefreshWeather)
        }
    }
}

@Composable
fun PortraitHud(uiState: HudUiState, onStartStop: () -> Unit, onRefreshWeather: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HudBlock(label = "CZAS JAZDY", value = formatElapsed(uiState.elapsedSeconds), isHighlight = uiState.rideState == RideState.RUNNING)
        HudBlock(label = "GODZINA", value = uiState.currentTime)
        
        LargeStartStopButton(state = uiState.rideState, onClick = onStartStop)
        if (uiState.isAutoPaused) {
            Text("AUTOPAUZA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                WeatherBlock(label = "TERAZ", weather = uiState.weatherNow, onRefresh = onRefreshWeather)
            }
            Box(modifier = Modifier.weight(1f)) {
                WeatherBlock(label = "ZA GODZINĘ", weather = uiState.weatherInHour, onRefresh = null)
            }
        }
    }
}

@Composable
fun LandscapeHud(uiState: HudUiState, onStartStop: () -> Unit, onRefreshWeather: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            HudBlock(label = "CZAS JAZDY", value = formatElapsed(uiState.elapsedSeconds), isHighlight = uiState.rideState == RideState.RUNNING)
            Spacer(modifier = Modifier.height(8.dp))
            HudBlock(label = "GODZINA", value = uiState.currentTime)
        }

        Column(modifier = Modifier.weight(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
            LargeStartStopButton(state = uiState.rideState, onClick = onStartStop)
            if (uiState.isAutoPaused) {
                Text("AUTOPAUZA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            WeatherBlock(label = "TERAZ", weather = uiState.weatherNow, onRefresh = onRefreshWeather)
            Spacer(modifier = Modifier.height(8.dp))
            WeatherBlock(label = "ZA GODZINĘ", weather = uiState.weatherInHour, onRefresh = null)
        }
    }
}

@Composable
fun WeatherBlock(label: String, weather: WeatherInfo?, onRefresh: (() -> Unit)?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            if (onRefresh != null) {
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                ) {
                    Icon(androidx.compose.material.icons.filled.Refresh, contentDescription = "Odśwież", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(weather?.emoji ?: "🌡️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(weather?.tempFormatted ?: "--°C", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Text(weather?.description ?: "brak danych", fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1)
            }
        }
    }
}

@Composable
fun LargeStartStopButton(state: RideState, onClick: () -> Unit) {
    val color = if (state == RideState.STOPPED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val icon = if (state == RideState.STOPPED) Icons.Default.PlayArrow else Icons.Default.Stop
    val text = if (state == RideState.STOPPED) "START" else "STOP"

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.size(120.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp))
            Text(text, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}

private fun formatElapsed(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}
