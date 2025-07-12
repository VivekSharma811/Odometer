package com.ather.odometer.odometer.presentation

/**
 * UI Effect for Odometer Screen
 * */
sealed class OdometerUIEffect {

    data object OpenDashboardApp: OdometerUIEffect()
}
