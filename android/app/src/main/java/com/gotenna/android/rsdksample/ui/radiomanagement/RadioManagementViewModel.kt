package com.gotenna.android.rsdksample.ui.radiomanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.gotenna.android.rsdksample.RadioManager
import com.gotenna.android.rsdksample.utils.launchWithLoading
import com.gotenna.android.rsdksample.utils.trigger
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
        onClickConnect = ::connect,
    )

    val eventNavToActions: MutableSharedFlow<Unit> = MutableSharedFlow()
    private val radios: MutableStateFlow<List<RadioModel>> = MutableStateFlow(emptyList())

    private fun scanBle() = launchWithLoading {
        radios.update { RadioManager.scan(ConnectionType.BLE) }
    }

    private fun scanUsb() = launchWithLoading {
        radios.update { RadioManager.scan(ConnectionType.USB) }
    }

    private fun connect(radio: RadioModel) = launchWithLoading {
        RadioManager.connect(radio)
        radios.update { emptyList() }
        eventNavToActions.trigger()
    }
}