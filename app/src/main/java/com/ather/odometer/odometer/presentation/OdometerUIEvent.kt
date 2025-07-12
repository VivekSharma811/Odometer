package com.ather.odometer.odometer.presentation

/**
 * UI Event class for Odometer Screen
 * */
sealed class OdometerUIEvent {

    data object OnDashboardCTAClicked : OdometerUIEvent()
}