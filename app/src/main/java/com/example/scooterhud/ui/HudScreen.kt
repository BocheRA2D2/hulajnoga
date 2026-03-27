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
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
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

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Lewa kolumna: Timer i Czas
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HudBlock(
                    label = "CZAS JAZDY",
                    value = formatElapsed(uiState.elapsedSeconds),
                    isHighlight = uiState.rideState == RideState.RUNNING
                )
                Spacer(modifier = Modifier.height(16.dp))
                HudBlock(
                    label = "GODZINA",
                    value = uiState.currentTime
                )
            }

            // ── Środek: Przycisk START/STOP
            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LargeStartStopButton(
                    state = uiState.rideState,
                    onClick = onStartStop
                )
                if (uiState.isAutoPaused) {
                    Text(
                        "AUTOPAUZA",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // ── Prawa kolumna: Pogoda
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherBlock(
                    label = "POGODA TERAZ",
                    weather = uiState.weatherNow
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherBlock(
                    label = "ZA GODZINĘ",
                    weather = uiState.weatherInHour
                )
            }
        }
    }
}

@Composable
fun HudBlock(label: String, value: String, isHighlight: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(
                value,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun WeatherBlock(label: String, weather: WeatherInfo?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(weather?.emoji ?: "🌡️", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    weather?.tempFormatted ?: "--°C",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                weather?.description ?: "brak danych",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
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
