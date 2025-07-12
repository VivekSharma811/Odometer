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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
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
    openDashboardApp: () -> Unit,
    viewModel: OdometerViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest {
            when (it) {
                OdometerUIEffect.OpenDashboardApp -> {
                    openDashboardApp()
                }
            }
        }
    }

    OdometerScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        state = state,
        textColor = Color.Black,
        onUIEvent = viewModel::onUIEvent
    )
}

@Composable
fun OdometerScreen(
    modifier: Modifier = Modifier,
    state: OdometerUIState,
    textColor: Color,
    onUIEvent: (OdometerUIEvent) -> Unit
) {
    Box(modifier = modifier) {
        Text(
            text = "Trip - ${state.data.trip}",
            modifier = Modifier.align(Alignment.TopEnd),
            color = textColor
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Speed - ${state.data.speed} KM/h",
                color = textColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Range - ${state.data.range} KM/h",
                color = textColor
            )
            if (state.showCTA) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { onUIEvent(OdometerUIEvent.OnDashboardCTAClicked) }) {
                    Text(text = "Go to Dashboard")
                }
            }
        }
        Text(
            text = "Odo - ${state.data.odo} KM",
            modifier = Modifier.align(Alignment.BottomStart),
            color = textColor
        )
        Text(
            text = "Mode - ${state.data.mode}",
            modifier = Modifier.align(Alignment.BottomEnd),
            color = textColor
        )
    }
}
