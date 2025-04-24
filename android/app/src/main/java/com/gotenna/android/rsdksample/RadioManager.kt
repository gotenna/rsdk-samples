package com.gotenna.android.rsdksample

import android.util.Log
import android.widget.Toast
import com.gotenna.android.rsdksample.utils.Global
import com.gotenna.radio.sdk.GotennaClient
import com.gotenna.radio.sdk.common.configuration.GTBandwidth
import com.gotenna.radio.sdk.common.configuration.GTFrequencyChannel
import com.gotenna.radio.sdk.common.configuration.GTPowerLevel
import com.gotenna.radio.sdk.common.models.CommandMetaData
import com.gotenna.radio.sdk.common.models.ConnectionType
import com.gotenna.radio.sdk.common.models.GTMessageType
import com.gotenna.radio.sdk.common.models.GotennaHeaderWrapper
import com.gotenna.radio.sdk.common.models.MessageTypeWrapper
import com.gotenna.radio.sdk.common.models.RadioModel
import com.gotenna.radio.sdk.common.models.SendToNetwork
import com.gotenna.radio.sdk.utils.executedOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Manages goTenna radio commands
 */
object RadioManager {
    private val connectedRadio: MutableStateFlow<RadioModel?> = MutableStateFlow(null)
    private var sessionId: String = ""
    private var messageReceiverJob: Job? = null

    val sessionCallsign: String get() = sessionId.take(6)
    private val _contacts: MutableMap<String, Long> = mutableMapOf()
    val contacts: Map<String, Long> = _contacts

    suspend fun scan(connectionType: ConnectionType) =
        GotennaClient.scan(connectionType)

    suspend fun connect(radio: RadioModel) {
        val tempConnectedRadio = connectedRadio.value
        if (radio == tempConnectedRadio) {
            return
        }
        tempConnectedRadio?.disconnect()
        radio.connect()

        sessionId = UUID.randomUUID().toString()

        connectedRadio.update { radio }
    }

    fun startMessageReceiver() {
        messageReceiverJob?.cancel()
        messageReceiverJob = Global.applicationScope.launch {
            connectedRadio.value?.radioEvents?.collect { event ->
                when (val message = event.executedOrNull()) {
                    // Received PLI
                    is SendToNetwork.Location -> {
                        val callsign = message.commandHeader.senderCallsign
                        val gid = message.commandHeader.senderGid
                        val log = "Received PLI from $callsign"
                        Log.d("RadioManager", log)
                        _contacts[callsign] = gid
                        Toast.makeText(SampleApplication.context, log, Toast.LENGTH_SHORT).show()
                    }

                    // Received Chat Message
                    is SendToNetwork.ChatMessage -> {
                        val callsign = message.commandHeader.senderCallsign
                        val text = message.text
                        val type = if (message.commandMetaData.messageType == GTMessageType.PRIVATE) "private" else "broadcast"
                        val log = "Received $type chat message from $callsign: $text"
                        Log.d("RadioManager", log)
                        Toast.makeText(SampleApplication.context, log, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun stopMessageReceiver() {
        messageReceiverJob?.cancel()
    }

    suspend fun disconnect() {
        connectedRadio.value?.disconnect()
        connectedRadio.update { null }
        messageReceiverJob = null
        sessionId = ""
    }

    suspend fun setFrequencyChannels(channels: List<GTFrequencyChannel>) =
        connectedRadio.value?.setFrequencyChannels(channels)

    suspend fun setPowerAndBandwidth(powerLevel: GTPowerLevel, bandwidth: GTBandwidth) =
        connectedRadio.value?.setPowerAndBandwidth(power = powerLevel, bandwidth = bandwidth)

    suspend fun flashLed() =
        connectedRadio.value?.performLedBlink()

    suspend fun sendPli() =
        connectedRadio.value?.run {
            send(
                SendToNetwork.Location(
                    how = "h-e",
                    staleTime = 60,
                    lat = 35.291802,
                    long = 80.846604,
                    altitude = 237.21325546763973,
                    team = "CYAN",
                    accuracy = 11,
                    commandMetaData = CommandMetaData(
                        messageType = GTMessageType.BROADCAST,
                        senderGid = personalGid,
                    ),
                    commandHeader = GotennaHeaderWrapper(
                        messageTypeWrapper = MessageTypeWrapper.LOCATION,
                        senderUUID = sessionId,
                        senderGid = personalGid,
                        senderCallsign = sessionCallsign,
                    ),
                )
            )
        }

    suspend fun sendBroadcastChatMessage() =
        connectedRadio.value?.run {
            send(
                SendToNetwork.ChatMessage(
                    commandMetaData = CommandMetaData(
                        messageType = GTMessageType.BROADCAST,
                        senderGid = personalGid,
                    ),
                    commandHeader = GotennaHeaderWrapper(
                        messageTypeWrapper = MessageTypeWrapper.BROADCAST_MESSAGE,
                        senderUUID = sessionId,
                        senderGid = personalGid,
                        senderCallsign = sessionCallsign,
                    ),
                    text = "broadcast message",
                    chatId = 12345,
                    chatMessageId = UUID.randomUUID().toString(),
                )
            )
        }

    suspend fun sendPrivateChatMessage(destinationGid: Long) =
        connectedRadio.value?.run {
            send(
                SendToNetwork.ChatMessage(
                    commandMetaData = CommandMetaData(
                        messageType = GTMessageType.PRIVATE,
                        destinationGid = destinationGid,
                        senderGid = personalGid,
                    ),
                    commandHeader = GotennaHeaderWrapper(
                        messageTypeWrapper = MessageTypeWrapper.CHAT_MESSAGE,
                        senderUUID = sessionId,
                        senderGid = personalGid,
                        senderCallsign = sessionCallsign,
                    ),
                    text = "private message",
                    chatId = 67890,
                    chatMessageId = UUID.randomUUID().toString(),
                )
            )
        }
}