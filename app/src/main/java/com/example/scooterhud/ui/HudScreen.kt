package com.example.scooterhud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.scooterhud.data.LayoutState
import com.example.scooterhud.data.model.WeatherInfo
import com.example.scooterhud.viewmodel.HudUiState
import com.example.scooterhud.viewmodel.RideState
import kotlin.math.roundToInt

@Composable
fun HudScreen(
    uiState: HudUiState,
    onStartStop: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefreshWeather: () -> Unit,
    onUpdateLayout: (String, Float, Float, Float) -> Unit = { _, _, _, _ -> }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        if (uiState.isEditMode) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    "TRYB EDYCJI: Przeciągnij boki. Użyj +/- do skali.",
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onOpenSettings, // W trybie edycji dajemy łatwy powrót do ustawień
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                ) {
                    Text("Zakończ edycję / Ustawienia")
                }
            }
        }

        if (uiState.isPortrait) {
            PortraitHud(uiState, onStartStop, onOpenSettings, onRefreshWeather, onUpdateLayout)
        } else {
            LandscapeHud(uiState, onStartStop, onOpenSettings, onRefreshWeather, onUpdateLayout)
        }
    }
}

@Composable
fun PortraitHud(
    uiState: HudUiState,
    onStartStop: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefreshWeather: () -> Unit,
    onUpdateLayout: (String, Float, Float, Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        EditorWrapper("timer", uiState, onUpdateLayout) {
            HudBlock(label = "CZAS JAZDY", value = formatElapsed(uiState.elapsedSeconds), isHighlight = uiState.rideState == RideState.RUNNING)
        }
        
        EditorWrapper("clock", uiState, onUpdateLayout) {
            HudBlock(
                label = "GODZINA", 
                value = uiState.currentTime, 
                onDoubleClick = onOpenSettings
            )
        }

        EditorWrapper("battery", uiState, onUpdateLayout) {
            BatteryBlock(level = uiState.batteryLevel, isCharging = uiState.isCharging)
        }
        
        EditorWrapper("button", uiState, onUpdateLayout) {
            LargeStartStopButton(state = uiState.rideState, onClick = onStartStop)
        }

        if (uiState.isAutoPaused) {
            Text("AUTOPAUZA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                EditorWrapper("weather_now", uiState, onUpdateLayout) {
                    WeatherBlock(label = "TERAZ", weather = uiState.weatherNow, onRefresh = onRefreshWeather)
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                EditorWrapper("weather_future", uiState, onUpdateLayout) {
                    WeatherBlock(label = "ZA GODZINĘ", weather = uiState.weatherInHour, onRefresh = null)
                }
            }
        }
    }
}

@Composable
fun LandscapeHud(
    uiState: HudUiState,
    onStartStop: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefreshWeather: () -> Unit,
    onUpdateLayout: (String, Float, Float, Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            EditorWrapper("timer", uiState, onUpdateLayout) {
                HudBlock(label = "CZAS JAZDY", value = formatElapsed(uiState.elapsedSeconds), isHighlight = uiState.rideState == RideState.RUNNING)
            }
            Spacer(modifier = Modifier.height(8.dp))
            EditorWrapper("clock", uiState, onUpdateLayout) {
                HudBlock(
                    label = "GODZINA", 
                    value = uiState.currentTime,
                    onDoubleClick = onOpenSettings
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            EditorWrapper("battery", uiState, onUpdateLayout) {
                BatteryBlock(level = uiState.batteryLevel, isCharging = uiState.isCharging)
            }
        }

        Column(modifier = Modifier.weight(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
            EditorWrapper("button", uiState, onUpdateLayout) {
                LargeStartStopButton(state = uiState.rideState, onClick = onStartStop)
            }
            if (uiState.isAutoPaused) {
                Text("AUTOPAUZA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            EditorWrapper("weather_now", uiState, onUpdateLayout) {
                WeatherBlock(label = "TERAZ", weather = uiState.weatherNow, onRefresh = onRefreshWeather)
            }
            Spacer(modifier = Modifier.height(8.dp))
            EditorWrapper("weather_future", uiState, onUpdateLayout) {
                WeatherBlock(label = "ZA GODZINĘ", weather = uiState.weatherInHour, onRefresh = null)
            }
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
                    Icon(Icons.Default.Refresh, contentDescription = "Odśwież", tint = MaterialTheme.colorScheme.primary)
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

@Composable
fun HudBlock(
    label: String, 
    value: String, 
    isHighlight: Boolean = false,
    onDoubleClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .then(
                if (onDoubleClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { onDoubleClick() })
                    }
                } else Modifier
            )
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
fun BatteryBlock(level: Int, isCharging: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("BATERIA", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isCharging) "⚡$level%" else "$level%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    level < 20 -> MaterialTheme.colorScheme.error
                    isCharging -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
fun EditorWrapper(
    key: String,
    uiState: HudUiState,
    onUpdate: (String, Float, Float, Float) -> Unit,
    content: @Composable () -> Unit
) {
    val layout = uiState.layouts[key] ?: LayoutState()
    
    Box(
        modifier = Modifier
            .offset { IntOffset(layout.offsetX.roundToInt(), layout.offsetY.roundToInt()) }
            .graphicsLayer {
                scaleX = layout.scale
                scaleY = layout.scale
            }
            .then(
                if (uiState.isEditMode) {
                    Modifier.pointerInput(key) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onUpdate(key, 0f, dragAmount.x, dragAmount.y)
                        }
                    }
                } else Modifier
            )
    ) {
        content()
        
        if (uiState.isEditMode) {
            // Kontrolki skalowania
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                IconButton(onClick = { onUpdate(key, -0.1f, 0f, 0f) }, modifier = Modifier.size(24.dp)) {
                    Text("-", color = Color.White, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { onUpdate(key, 0.1f, 0f, 0f) }, modifier = Modifier.size(24.dp)) {
                    Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatElapsed(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}
