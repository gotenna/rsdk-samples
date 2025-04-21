package com.gotenna.android.rsdksample.ui.radiomanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.gotenna.android.rsdksample.RadioManager
import com.gotenna.android.rsdksample.RadioManager.setFrequencyChannels
import com.gotenna.android.rsdksample.RadioManager.setPowerAndBandwidth
import com.gotenna.android.rsdksample.utils.launchWithLoading
import com.gotenna.android.rsdksample.utils.trigger
import com.gotenna.radio.sdk.common.configuration.GTBandwidth
import com.gotenna.radio.sdk.common.configuration.GTFrequencyChannel
import com.gotenna.radio.sdk.common.configuration.GTPowerLevel
import com.gotenna.radio.sdk.common.models.ConnectionType
import com.gotenna.radio.sdk.common.models.RadioModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class RadioManagementViewModel : ViewModel() {

    @Composable
    fun toState() = RadioManagementState(
        radios = radios.collectAsState(),
        onClickScanBle = ::scanBle,
        onClickScanUsb = ::scanUsb,
        onClickConnect = ::connectAndInitialize,
    )

    val eventNavToActions: MutableSharedFlow<Unit> = MutableSharedFlow()
    private val radios: MutableStateFlow<List<RadioModel>> = MutableStateFlow(emptyList())

    private fun scanBle() = launchWithLoading {
        radios.update { RadioManager.scan(ConnectionType.BLE) }
    }

    private fun scanUsb() = launchWithLoading {
        radios.update { RadioManager.scan(ConnectionType.USB) }
    }

    private fun connectAndInitialize(radio: RadioModel) = launchWithLoading {
        RadioManager.connect(radio)

        setPowerAndBandwidth(
            powerLevel = GTPowerLevel.ONE,
            bandwidth = GTBandwidth.BANDWIDTH_11_8,
        )

        // Note: Values used here is only for example.
        // Please use your Part 90 allocated or local regulatory body's allowed frequencies.
        setFrequencyChannels(
            channels = listOf(
                // Data channels
                GTFrequencyChannel(
                    148000000,
                    isControlChannel = false,
                ),
                GTFrequencyChannel(
                    149000000,
                    isControlChannel = false,
                ),

                // Control channels
                GTFrequencyChannel(
                    158000000,
                    isControlChannel = true,
                ),
                GTFrequencyChannel(
                    159000000,
                    isControlChannel = true,
                ),
            )
        )

        radios.update { emptyList() }
        eventNavToActions.trigger()
    }
}