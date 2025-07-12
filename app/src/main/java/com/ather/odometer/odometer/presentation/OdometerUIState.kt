package com.ather.odometer.odometer.presentation

import androidx.compose.runtime.Stable
import com.ather.odometer.odometer.data.OdometerReading

/**
 * UI State class for Odometer Screen
 * */
@Stable
data class OdometerUIState(
    val showCTA: Boolean = true,
    val data: OdometerReading = OdometerReading()
)
