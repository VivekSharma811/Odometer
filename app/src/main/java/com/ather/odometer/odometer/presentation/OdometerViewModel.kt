package com.ather.odometer.odometer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ather.odometer.odometer.data.OdometerReading
import com.ather.odometer.odometer.domain.SendOdometerBroadcastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OdometerViewModel @Inject constructor(
    private val sendBroadcast: SendOdometerBroadcastUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OdometerUIState())
    val uiState = _uiState
        .onStart { generateData() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            OdometerUIState()
        )

    private var _uiEffect = Channel<OdometerUIEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(2000)
                val newData = generateData()
                withContext(Dispatchers.Main.immediate) {
                    _uiState.update { it.copy(data = newData) }
                    sendBroadcast(newData)
                }
            }
        }
    }

    fun onUIEvent(event: OdometerUIEvent) {
        viewModelScope.launch {
            when (event) {
                OdometerUIEvent.OnDashboardCTAClicked -> {
                    _uiState.update { it.copy(showCTA = false) }
                    _uiEffect.send(OdometerUIEffect.OpenDashboardApp)
                }
            }
        }
    }

    private fun generateData(): OdometerReading {
        return OdometerReading(
            speed = (0..120).random(),
            odo = (1000..20000).random().toFloat(),
            range = (10..100).random(),
            mode = listOf("Eco", "Ride", "Warp").random(),
            trip = (0..150).random().toFloat()
        )
    }
}
