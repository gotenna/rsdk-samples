package com.gotenna.android.rsdksample.ui.radioactions

import androidx.lifecycle.ViewModel
import com.gotenna.android.rsdksample.RadioManager
import com.gotenna.android.rsdksample.utils.launchWithLoading

class RadioActionsViewModel : ViewModel() {

    fun toState() = RadioActionsState(
        onClickFlashLed = ::flashLed,
        onClickSendPli = ::sendPli,
        onClickSendBroadcastChat = ::sendBroadcastChatMessage,
        onClickSendPrivateChat = ::sendPrivateChatMessage,
    )

    fun flashLed() = launchWithLoading {
        RadioManager.flashLed()
    }

    fun sendPli() = launchWithLoading {
        RadioManager.sendPli()
    }

    fun sendBroadcastChatMessage() = launchWithLoading {
        RadioManager.sendChatMessage()
    }

    fun sendPrivateChatMessage() = launchWithLoading {
        // TODO
    }

    fun disconnect() = launchWithLoading {
        RadioManager.disconnect()
    }
}