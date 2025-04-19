package com.gotenna.android.rsdksample

import com.gotenna.radio.sdk.GotennaClient
import com.gotenna.radio.sdk.common.configuration.GTBandwidth
import com.gotenna.radio.sdk.common.configuration.GTFrequencyChannel
import com.gotenna.radio.sdk.common.configuration.GTPowerLevel
import com.gotenna.radio.sdk.common.configuration.GidType
import com.gotenna.radio.sdk.common.models.CommandMetaData
import com.gotenna.radio.sdk.common.models.ConnectionType
import com.gotenna.radio.sdk.common.models.GTMessagePriority
import com.gotenna.radio.sdk.common.models.GTMessageType
import com.gotenna.radio.sdk.common.models.GotennaHeaderWrapper
import com.gotenna.radio.sdk.common.models.MessageTypeWrapper
import com.gotenna.radio.sdk.common.models.RadioModel
import com.gotenna.radio.sdk.common.models.SendToNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Manages Gotenna radio commands
 */
object RadioManager {
    private val connectedRadio: MutableStateFlow<RadioModel?> = MutableStateFlow(null)
    private var sessionId: String = ""
    private val sessionCallsign: String get() = sessionId.take(6)

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

        setGid(
            radio = radio,
            gid = radio.personalGid,
        )

        setPowerAndBandwidth(
            radio = radio,
            powerLevel = GTPowerLevel.ONE,
            bandwidth = GTBandwidth.BANDWIDTH_11_8,
        )

        setFrequencyChannels(
            radio = radio,
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

        connectedRadio.update { radio }
    }

    suspend fun disconnect() {
        connectedRadio.value?.disconnect()
        connectedRadio.update { null }
        sessionId = ""
    }

    suspend fun setGid(radio: RadioModel, gid: Long) =
        radio.setGid(
            gid = gid,
            type = GidType.PRIVATE,
        )

    suspend fun setFrequencyChannels(radio: RadioModel, channels: List<GTFrequencyChannel>) =
        radio.setFrequencyChannels(channels)

    suspend fun setPowerAndBandwidth(radio: RadioModel, powerLevel: GTPowerLevel, bandwidth: GTBandwidth) =
        radio.setPowerAndBandwidth(power = powerLevel, bandwidth = bandwidth)

    suspend fun flashLed() =
        connectedRadio.value?.performLedBlink()

    suspend fun sendPli() =
        connectedRadio.value?.send(
            SendToNetwork.Location(
                how = "h-e",
                staleTime = 60,
                lat = 35.291802,
                long = 80.846604,
                altitude = 237.21325546763973,
                team = "CYAN",
                accuracy = 11,
                creationTime = System.currentTimeMillis(),
                messageId = 0,
                commandMetaData = CommandMetaData(messageType= GTMessageType.BROADCAST, destinationGid=0, isPeriodic=false, priority= GTMessagePriority.NORMAL, senderGid=904610228241489),
                commandHeader = GotennaHeaderWrapper(timeStamp=1718745135761, messageTypeWrapper= MessageTypeWrapper.LOCATION, appCode=0, senderGid=904610228241489, senderUUID="ANDROID-2440142b8ac6d5d7", senderCallsign="JONAS", encryptionParameters=null),
            )
        )

    suspend fun sendChatMessage(privateMessage: Boolean = false, destinationGid: Long = 0) =
        connectedRadio.value?.run {
            send(
                SendToNetwork.ChatMessage(
                    commandMetaData = CommandMetaData(
                        messageType = if (privateMessage) GTMessageType.PRIVATE else GTMessageType.BROADCAST,
                        destinationGid = if (privateMessage) destinationGid else 0L,
                        senderGid = personalGid
                    ),
                    commandHeader = GotennaHeaderWrapper(
                        uuid = UUID.randomUUID().toString(),
                        senderGid = personalGid,
                        senderCallsign = sessionCallsign,
                        messageTypeWrapper = MessageTypeWrapper.CHAT_MESSAGE,
                        appCode = 123,
                        recipientUUID = UUID.randomUUID().toString(),
                        senderUUID = sessionId,
                        encryptionParameters = null,
                    ),
                    text = "chat message",
                    chatId = 12345,
                    conversationId = UUID.randomUUID().toString(),
                    conversationName = "conversation name",
                    chatMessageId = UUID.randomUUID().toString()
                )
            )
        }
}