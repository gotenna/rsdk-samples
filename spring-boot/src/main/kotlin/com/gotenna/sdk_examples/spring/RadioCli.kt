package com.gotenna.sdk_examples.spring

import com.gotenna.radio.sdk.GotennaClient
import com.gotenna.radio.sdk.common.configuration.GTBandwidth
import com.gotenna.radio.sdk.common.configuration.GTFrequencyChannel
import com.gotenna.radio.sdk.common.configuration.GTPowerLevel
import com.gotenna.radio.sdk.common.models.CommandMetaData
import com.gotenna.radio.sdk.common.models.ConnectionType
import com.gotenna.radio.sdk.common.models.Coordinate
import com.gotenna.radio.sdk.common.models.GTMessageType
import com.gotenna.radio.sdk.common.models.GotennaHeaderWrapper
import com.gotenna.radio.sdk.common.models.MapObject
import com.gotenna.radio.sdk.common.models.MessageTypeWrapper
import com.gotenna.radio.sdk.common.models.RadioModel
import com.gotenna.radio.sdk.common.models.SendToNetwork
import com.gotenna.radio.sdk.utils.executedOrNull
import com.gotenna.radio.sdk.utils.getErrorOrNull
import com.gotenna.radio.sdk.utils.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import org.springframework.shell.standard.ShellCommandGroup
import java.nio.charset.Charset
import java.util.UUID
import kotlin.random.Random

@Command(command = ["radio"])
@ShellCommandGroup("Radio interactions")
class RadioCli {

    private var devices: MutableList<RadioModel> = mutableListOf()
    private var destination: Long = 0L

    init {
        val token = System.getenv("SDK_TOKEN")
        val appId = System.getenv("SDK_APP_ID")

        CoroutineScope(Dispatchers.IO).launch {
            GotennaClient.initialize(
                sdkToken = token,
                appId = appId,
                preProcessAction = { bytes, encryptionParams, senderGid ->
                    // encrypt outgoing messages
                    /*
                        encryptionParams?.keyUuid
                        encryptionParams?.iv
                    */
                    bytes // return the processed bytes
                },
                postProcessAction = { bytes, header ->
                    // decrypt incoming messages
                    /*
                        header.encryptionParameters?.keyUuid
                        header.encryptionParameters?.iv
                    */
                    bytes // return the processed bytes
                },
                enableDebugLogs = true
            )
        }
    }

    @Command(
        command = ["scan"],
        description = "This will attempt to scan for any radios that are connected."
    )
    fun scan(@Option(longNames = ["connectionType"], required = true) connectionType: ConnectionType): List<RadioModel> {
        return runBlocking {
            devices = GotennaClient.scan(connectionType).toMutableList()
            devices.forEach {
                println("Found radio: ${it.serialNumber}, address: ${it.address}")
            }
            devices
        }
    }

    @Command(
        command = ["connect"],
        description = "This will attempt to connect to the first scanned device."
    )
    fun connect(): Boolean {
        return runBlocking {
            devices.first().connect().isSuccess()
        }
    }

    @Command(
        command = ["scanAndConnect"],
        description = "This will both scan and attempt to connect to the first returned device."
    )
    fun scanAndConnect() {
        println("starting cli scan")
        runBlocking {
            devices = GotennaClient.scan(ConnectionType.USB).toMutableList()
            devices.firstOrNull()?.connect()
        }
    }

    @Command(
        command = ["blink"],
        description = "This will make the led blink on the radio."
    )
    fun blink() {
        println("Size of devices: ${devices.size}")
        runBlocking {
            devices.firstOrNull()?.performLedBlink()
        }
    }

    @Command(
        command = ["disconnect"],
        description = "This will disconnect all radio's"
    )
    fun disconnect() {
        runBlocking {
            println("disconnecting devices, size of devices: ${devices.size}")
            devices.forEach {
                println("disconnecting ${it.serialNumber}")
                it.disconnect()
            }
        }
    }

    @Command(
        command = ["power"],
        description = "This will set the power and bandwidth of the first radio."
    )
    fun setPowerAndBandwidth(
        @Option(longNames = ["powerLevel"], required = true)
        powerLevel: GTPowerLevel,
        @Option(longNames = ["bandwidth"], required = true)
        bandwidth: GTBandwidth
    ) {
        println("setting power $powerLevel and bandwidth $bandwidth")
        runBlocking {
            devices.firstOrNull()?.setPowerAndBandwidth(
                power = powerLevel,
                bandwidth = bandwidth
            )
        }
    }

    @Command(
        command = ["frequencies"],
        description = "This will set the frequencies of the first radio."
    )
    fun setFrequencies(
        @Option(
            longNames = ["control"],
            required = true,
            description = "The frequency in hz of the control channels",
            arityMin = 1,
            arityMax = 3
        )
        controlChannelFrequenciesInHz: List<String>,
        @Option(
            longNames = ["data"],
            description = "The frequency in hz of the data channels",
            arityMin = 1,
            arityMax = 13
        )
        dataChannelFrequenciesInHz: List<String>
    ) {
        val channels = mutableListOf<GTFrequencyChannel>()
        try {
            val controlChannels = controlChannelFrequenciesInHz.map {
                GTFrequencyChannel(
                    frequencyHz = it.toInt(), isControlChannel = true
                )
            }
            if (controlChannels.all { it.isValid }) {
                channels.addAll(controlChannels)
            }
        } catch (e: Exception) {
            println("The supplied control channels were not valid $controlChannelFrequenciesInHz")
            return
        }

        try {
            val dataChannels = dataChannelFrequenciesInHz.map {
                GTFrequencyChannel(
                    frequencyHz = it.toInt(), isControlChannel = false
                )
            }
            if (dataChannels.all { it.isValid }) {
                channels.addAll(dataChannels)
            }
        } catch (e: Exception) {
            println("The supplied data channels were not valid $dataChannelFrequenciesInHz")
            return
        }
        runBlocking {
            devices.firstOrNull()?.setFrequencyChannels(
                channels
            )
        }
    }

    @Command(
        command = ["listen"],
        description = "Subscribes to the events from the radio and outputs them to console."
    )
    fun listenToEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            devices.firstOrNull()?.radioEvents?.collect {
                if (it.isSuccess()) {
                    println("Success: ${it.executedOrNull()}\n")
                } else {
                    println("Error: ${it.getErrorOrNull()}\n")
                }
            }
        }
    }

    @Command(
        command = ["destination"],
        description = "Set the destination gid for 1-1 messages."
    )
    fun setDestinationGid(
        @Option(required = true)
        destination: Long) {
        this.destination = destination
    }

    @Command(
        command = ["pli"],
        description = "Send a randomized pli message to the mesh."
    )
    fun sendPli(
        @Option(longNames = ["unicast"], required = false)
        privateMessage: Boolean = false,
    ) {
        runBlocking {
            devices.firstOrNull()?.send(
                SendToNetwork.Location(
                    how = "m-g",
                    staleTime = 300,
                    lat = Random.nextDouble(),
                    long = Random.nextDouble(),
                    altitude = 0.0,
                    team = "CYAN",
                    commandMetaData = CommandMetaData(
                        messageType = if (privateMessage) GTMessageType.PRIVATE else GTMessageType.BROADCAST,
                        destinationGid = if (privateMessage) destination else 0,
                        isPeriodic = false,
                        senderGid = devices.firstOrNull()?.personalGid ?: 0
                    ),
                    commandHeader = GotennaHeaderWrapper(
                        messageTypeWrapper = MessageTypeWrapper.LOCATION,
                        recipientUUID = UUID.randomUUID().toString(),
                        senderGid = devices.firstOrNull()?.personalGid ?: 0,
                        senderUUID = UUID.randomUUID().toString(),
                        senderCallsign = "testJVMuser",
                        uuid = UUID.randomUUID().toString()
                    ),
                )
            )
        }
    }

    @Command(
        command = ["sendCircle"],
        description = "Send a circle map object to the mesh network."
    )
    fun sendMapObject() {
        runBlocking {
            devices.firstOrNull()?.send(
                MapObject(
                    title = "Example Circle Map Object",
                    commandHeader = GotennaHeaderWrapper(
                        messageTypeWrapper = MessageTypeWrapper.MAP_OBJECT,
                        recipientUUID = UUID.randomUUID().toString(),
                        senderGid = devices.firstOrNull()?.personalGid ?: 0,
                        senderUUID = UUID.randomUUID().toString(),
                        senderCallsign = "testJVMuser",
                        uuid = UUID.randomUUID().toString()
                    ),
                    data = MapObject.ObjectData.Circle(
                        centerPoint = Coordinate(
                            lat = Random.nextDouble(),
                            long = Random.nextDouble()
                        ),
                        radius = 1000.0
                    )
                )
            )
        }
    }

    fun sendGripFile(
        @Option(longNames = ["unicast"], required = false)
         privateMessage: Boolean = false,
    ) {
        runBlocking {
            devices.firstOrNull()?.send(
                SendToNetwork.GripFile(
                    data = List(4096) { ('a'..'z').random() }.joinToString("").toByteArray(
                        Charset.defaultCharset()
                    ), // 4kb size file
                    fileName = "testfile.txt",
                    numberOfSegments = 0,
                    partialData = false,
                    commandMetaData = CommandMetaData(
                        messageType = if (privateMessage) GTMessageType.PRIVATE else GTMessageType.BROADCAST,
                        destinationGid = if (privateMessage) destination else 0,
                        isPeriodic = false,
                        senderGid = devices.firstOrNull()?.personalGid ?: 0
                    ),
                    commandHeader = GotennaHeaderWrapper(
                        messageTypeWrapper = MessageTypeWrapper.GRIP_FILE,
                        recipientUUID = UUID.randomUUID().toString(),
                        senderGid = devices.firstOrNull()?.personalGid ?: 0,
                        senderUUID = UUID.randomUUID().toString(),
                        senderCallsign = "testJVMuser",
                        uuid = UUID.randomUUID().toString()
                    ),
                )
            )
        }
    }

}