package com.gotenna.sdk_examples.spring

import com.gotenna.radio.sdk.common.models.RadioCommand
import com.gotenna.radio.sdk.common.models.RadioModel
import com.gotenna.radio.sdk.common.models.RadioState
import com.gotenna.radio.sdk.utils.RadioResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JavaAdapter(private val radioModel: RadioModel) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    // Method to collect StateFlow and pass data to a Java callback
    fun collectRadioState(callback: StateUpdateCallback) = ioScope.launch {
        radioModel.radioState.collect { state ->
            callback.onStateUpdated(state)
        }
    }

    fun collectRadioEvents(callback: EventsCallback) = ioScope.launch {
        radioModel.radioEvents.collect { event ->
            callback.onEvent(event)
        }
    }
}

interface StateUpdateCallback {
    fun onStateUpdated(state: RadioState)
}

interface EventsCallback {
    fun onEvent(result: RadioResult<RadioCommand>)
}