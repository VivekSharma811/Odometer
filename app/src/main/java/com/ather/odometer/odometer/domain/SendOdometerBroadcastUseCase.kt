package com.ather.odometer.odometer.domain

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ather.odometer.odometer.data.OdometerReading
import com.ather.odometer.odometer.utils.DASHBOARD_APP_PACKAGE
import com.ather.odometer.odometer.utils.DATA
import com.ather.odometer.odometer.utils.ODOMETER_READING
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use Case to Broadcast Odometer data
 * */
@Singleton
class SendOdometerBroadcastUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(data: OdometerReading) {
        val intent = Intent(ODOMETER_READING)
        intent.`package` = DASHBOARD_APP_PACKAGE
        intent.putExtra(DATA, data)
        context.sendBroadcast(intent)
    }
}
