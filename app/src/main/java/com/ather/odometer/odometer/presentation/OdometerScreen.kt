package com.ather.odometer.odometer.presentation

import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ather.odometer.odometer.utils.DASHBOARD_APP_ACTIVITY
import com.ather.odometer.odometer.utils.DASHBOARD_APP_PACKAGE
import kotlinx.coroutines.flow.collectLatest

/**
 * UI Container for Odometer Screen
 * */
@Composable
fun OdometerScreen(
    viewModel: OdometerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest {
            when (it) {
                OdometerUIEffect.OpenDashboardApp -> {
                    try {
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            component = ComponentName(DASHBOARD_APP_PACKAGE, DASHBOARD_APP_ACTIVITY)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        ctx.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Could not find the Dashboard", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    OdometerScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent
    )
}

@Composable
private fun OdometerScreen(
    state: OdometerUIState,
    onUIEvent: (OdometerUIEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "${state.data.trip}",
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${state.data.speed} KM/h")
            Text(text = "${state.data.range} KM/h")
            if (state.showCTA) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { onUIEvent(OdometerUIEvent.OnDashboardCTAClicked) }) {
                    Text(text = "Go to Dashboard")
                }
            }
        }
        Text(
            text = "${state.data.odo} KM",
            modifier = Modifier.align(Alignment.BottomStart)
        )
        Text(
            text = "${state.data.trip} KM",
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
