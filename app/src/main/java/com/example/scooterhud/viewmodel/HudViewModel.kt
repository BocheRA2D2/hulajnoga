package com.example.scooterhud.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scooterhud.data.SettingsRepository
import com.example.scooterhud.data.WeatherRepository
import com.example.scooterhud.data.model.WeatherInfo
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

enum class RideState { STOPPED, RUNNING, PAUSED }

data class HudUiState(
    val rideState: RideState = RideState.STOPPED,
    val elapsedSeconds: Long = 0L,
    val currentTime: String = "--:--:--",
    val weatherNow: WeatherInfo? = null,
    val weatherInHour: WeatherInfo? = null,
    val isAutoPauseEnabled: Boolean = false,
    val isDarkTheme: Boolean = true,
    val isAutoPaused: Boolean = false,
    val isPortrait: Boolean = false
)

class HudViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val weatherRepo = WeatherRepository()
    private val fusedLocation = LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow(
        HudUiState(
            isAutoPauseEnabled = settingsRepo.isAutoPauseEnabled,
            isDarkTheme = settingsRepo.isDarkTheme,
            isPortrait = settingsRepo.isPortrait
        )
    )
    val uiState: StateFlow<HudUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var lastKnownLocation: android.location.Location? = null
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            handleAutoPause(location.speed)

            val prev = lastKnownLocation
            if (prev == null || location.distanceTo(prev) > 500f) {
                lastKnownLocation = location
                fetchWeather(location.latitude, location.longitude)
            }
        }
    }

    init {
        startClock()
        startPeriodicWeatherRefresh()
    }

    // ── Timer ──────────────────────────────────────────────────

    fun onStartStop() {
        when (_uiState.value.rideState) {
            RideState.STOPPED -> {
                _uiState.update { it.copy(rideState = RideState.RUNNING, elapsedSeconds = 0L, isAutoPaused = false) }
                startTimer()
            }
            RideState.RUNNING, RideState.PAUSED -> {
                stopTimer()
                _uiState.update { it.copy(rideState = RideState.STOPPED, isAutoPaused = false) }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    // ── Clock ──────────────────────────────────────────────────

    private fun startClock() {
        viewModelScope.launch {
            while (isActive) {
                _uiState.update { it.copy(currentTime = timeFormat.format(Date())) }
                delay(1000L)
            }
        }
    }

    // ── Weather ────────────────────────────────────────────────

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val result = weatherRepo.getWeather(lat, lon)
            result?.let { (now, inHour) ->
                _uiState.update { it.copy(weatherNow = now, weatherInHour = inHour) }
            }
        }
    }

    fun refreshWeather() {
        lastKnownLocation?.let { fetchWeather(it.latitude, it.longitude) }
    }

    private fun startPeriodicWeatherRefresh() {
        viewModelScope.launch {
            while (isActive) {
                delay(10 * 60 * 1000L) // co 10 minut
                lastKnownLocation?.let { fetchWeather(it.latitude, it.longitude) }
            }
        }
    }

    // ── Auto-pause ─────────────────────────────────────────────

    private fun handleAutoPause(speedMs: Float) {
        if (!_uiState.value.isAutoPauseEnabled) return
        val isMoving = speedMs > 0.5f // próg: ~1.8 km/h
        val state = _uiState.value.rideState

        when {
            state == RideState.RUNNING && !isMoving -> {
                stopTimer()
                _uiState.update { it.copy(rideState = RideState.PAUSED, isAutoPaused = true) }
            }
            state == RideState.PAUSED && _uiState.value.isAutoPaused && isMoving -> {
                _uiState.update { it.copy(rideState = RideState.RUNNING, isAutoPaused = false) }
                startTimer()
            }
        }
    }

    // ── Location ───────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val hasPermission = ContextCompat.checkSelfPermission(
            getApplication(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(3000L)
            .build()

        fusedLocation.requestLocationUpdates(request, locationCallback, null)

        fusedLocation.lastLocation.addOnSuccessListener { loc ->
            loc?.let {
                lastKnownLocation = it
                fetchWeather(it.latitude, it.longitude)
            }
        }
    }

    fun stopLocationUpdates() {
        fusedLocation.removeLocationUpdates(locationCallback)
    }

    // ── Settings ───────────────────────────────────────────────

    fun toggleAutoPause() {
        val new = !_uiState.value.isAutoPauseEnabled
        settingsRepo.isAutoPauseEnabled = new
        _uiState.update { it.copy(isAutoPauseEnabled = new, isAutoPaused = if (!new) false else it.isAutoPaused) }
        if (!new && _uiState.value.rideState == RideState.PAUSED && _uiState.value.isAutoPaused) {
            _uiState.update { it.copy(rideState = RideState.RUNNING) }
            startTimer()
        }
    }

    fun toggleTheme() {
        val new = !_uiState.value.isDarkTheme
        settingsRepo.isDarkTheme = new
        _uiState.update { it.copy(isDarkTheme = new) }
    }

    fun toggleOrientation() {
        val new = !_uiState.value.isPortrait
        settingsRepo.isPortrait = new
        _uiState.update { it.copy(isPortrait = new) }
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}
