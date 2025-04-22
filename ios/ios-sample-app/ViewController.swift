import UIKit
import RSDK

@MainActor
class ViewController: UIViewController {
    
    @IBOutlet weak var scanConnectDisconnectButton: UIButton!
    @IBOutlet weak var sendLocationButton: UIButton!
    @IBOutlet weak var sendBroadcastChatButton: UIButton!
    @IBOutlet weak var sendPrivateChatButton: UIButton!
    @IBOutlet weak var blinkLedButton: UIButton!
    
    private var activeRadio: RadioModel?
    private var radioConnectionState = RadioState.disconnected
    private let senderUuid = UUID().uuidString
    private let johnUuid = UUID().uuidString
    private let johnGid = Int64(90164571133865)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        updateUIState(.disconnected)
        
        Task {
            do {
                let initialized = try await GotennaClient.shared.initialize(
                    sdkToken: "<#YOUR_SDK_TOKEN#>",
                    appId: "<#YOUR_APP_ID#>",
                    preProcessAction: nil,
                    postProcessAction: nil,
                    enableDebugLogs: true
                )
                print("Initialized: \(initialized)")
            } catch {
                print("Error initializing: \(error)")
            }
        }
    }
    
    @IBAction func scanButtonTapped(_ sender: UIButton) {
        Task {
            do {
                if radioConnectionState == .disconnected {
                    updateUIState(.scanning)
                    let radios = try await GotennaClient.shared.scan(connectionType: ConnectionType.ble, address: nil)
                    activeRadio = radios.first
                    startObservingRadioState()
                    try await activeRadio?.connect()
                    
                    // Note: Values used here is only for example.
                    // Please use your Part 90 allocated or local regulatory body's allowed frequencies.
                    let controlChannel = GTFrequencyChannel(frequencyMhz: 455, isControlChannel: true)
                    let dataChannel = GTFrequencyChannel(frequencyMhz: 465, isControlChannel: false)
                    try await activeRadio?.setFrequencyChannels(channels: [controlChannel, dataChannel])
                    try await activeRadio?.setPowerAndBandwidth(power: GTPowerLevel.five, bandwidth: GTBandwidth.bandwidth118)
                    
                    updateUIState(.connected)
                    startObservingRadioEvents()
                } else {
                    try await activeRadio?.disconnect()
                    updateUIState(.disconnected)
                }
            } catch {
                print("Error scanning/connecting: \(error)")
            }
        }
    }
    
    @IBAction func sendLocationButtonTapped(_ sender: UIButton) {
        // NOTE: There are many default values below. These are values that usually clients wouldn't actually need to specify.
        // Unfortunately there is currently a Kotlin Multiplatform translation issue for Swift that drops the Kotlin default values,
        // so all must be explicitly specified.
        Task {
            let location = SendToNetwork.Location(
                how: "m-g",
                staleTime: 60,
                lat: 35.291802,
                long: 80.846604,
                altitude: 0.0,
                team: "CYAN",
                accuracy: 11, // default value
                creationTime: Date().millisecondsSinceEpoch,
                messageId: 0, // default value
                commandMetaData: CommandMetaData(
                    messageType: GTMessageType.broadcast,
                    destinationGid: 0, // default for broadcasts
                    isPeriodic: false, // default value
                    priority: GTMessagePriority.normal, // default value
                    senderGid: activeRadio?.personalGid ?? 0
                ),
                commandHeader: GotennaHeaderWrapper(
                    timeStamp: Date().millisecondsSinceEpoch,
                    messageTypeWrapper: MessageTypeWrapper.location,
                    recipientUUID: "",
                    appCode: 0, // default value
                    senderGid: activeRadio?.personalGid ?? 0,
                    senderUUID: senderUuid,
                    senderCallsign: "JONAS",
                    encryptionParameters: nil,
                    uuid: UUID().uuidString
                ),
                gripResult: GripResultUnknown(), // default value
                _bytes: nil, // default value
                sequenceId: -1 // default value
            )

             try await activeRadio?.send(model: location)
        }
    }
    
    @IBAction func sendBroadcastChatTapped(_ sender: UIButton) {
        Task {
            let chatMessage = SendToNetwork.ChatMessage(
                text: "Hello World",
                chatId: 1234,
                chatMessageId: UUID().uuidString,
                conversationId: nil,
                conversationName: nil,
                creationTime: Date().millisecondsSinceEpoch,
                messageId: 1234,
                commandMetaData: CommandMetaData(
                    messageType: GTMessageType.broadcast,
                    destinationGid: 0,
                    isPeriodic: false,
                    priority: GTMessagePriority.normal,
                    senderGid: activeRadio?.personalGid ?? 0
                ),
                commandHeader: GotennaHeaderWrapper(
                    timeStamp: Date().millisecondsSinceEpoch,
                    messageTypeWrapper: MessageTypeWrapper.chatMessage,
                    recipientUUID: "",
                    appCode: 0,
                    senderGid: activeRadio?.personalGid ?? 0,
                    senderUUID: senderUuid,
                    senderCallsign: "JONAS",
                    encryptionParameters: nil,
                    uuid: UUID().uuidString
                ),
                gripResult: GripResultUnknown(),
                _bytes: nil,
                sequenceId: -1
            )
            
            try await activeRadio?.send(model: chatMessage)
        }
    }
    
    @IBAction func sendPrivateChatTapped(_ sender: UIButton) {
        Task {
            let chatMessage = SendToNetwork.ChatMessage(
                text: "Hello JOHN",
                chatId: 1234,
                chatMessageId: UUID().uuidString,
                conversationId: johnUuid,
                conversationName: "JOHN",
                creationTime: Date().millisecondsSinceEpoch,
                messageId: 1234,
                commandMetaData: CommandMetaData(
                    messageType: GTMessageType.private_,
                    destinationGid: johnGid,
                    isPeriodic: false,
                    priority: GTMessagePriority.normal,
                    senderGid: activeRadio?.personalGid ?? 0
                ),
                commandHeader: GotennaHeaderWrapper(
                    timeStamp: Date().millisecondsSinceEpoch,
                    messageTypeWrapper: MessageTypeWrapper.chatMessage,
                    recipientUUID: johnUuid,
                    appCode: 0,
                    senderGid: activeRadio?.personalGid ?? 0,
                    senderUUID: senderUuid,
                    senderCallsign: "JONAS",
                    encryptionParameters: nil,
                    uuid: UUID().uuidString
                ),
                gripResult: GripResultUnknown(),
                _bytes: nil,
                sequenceId: -1
            )
            
            try await activeRadio?.send(model: chatMessage)
        }
    }
    
    @IBAction func blinkLedButtonTapped(_ sender: UIButton) {
        Task {
            do {
                try await activeRadio?.performLedBlink()
            } catch {
                print("Error performing LED blink: \(error)")
            }
        }
    }
    
    private func startObservingRadioState() {
        Task {
            try await activeRadio?.radioState.collect(collector: Collector<RadioState>(callback: { [weak self] newState in
                print("Radio state changed to: \(newState)")
                self?.radioConnectionState = newState
            }))
        }
    }
    
    private func startObservingRadioEvents() {
        Task {
            try await activeRadio?.radioEvents.collect(collector: Collector<RadioResult>(callback: { radioResult in
                if radioResult is RadioResultFailure<RadioCommand> { print("Got failure") }
                guard let success = radioResult as? RadioResultSuccess<RadioCommand>, let executed = success.executed else {
                    print("Unexpected radio result: \(radioResult)\n\n")
                    return
                }
                print("Got event from radio: \(executed)")
            }))
        }
    }
    
    private func updateUIState(_ state: UIState) {
        switch state {
        case .scanning:
            handleScanning()
        case .connected:
            handleConnected()
        case .disconnected:
            handleDisconnected()
        }
    }
    
    private func handleDisconnected() {
        sendLocationButton.isHidden = true
        sendBroadcastChatButton.isHidden = true
        sendPrivateChatButton.isHidden = true
        blinkLedButton.isHidden = true
        scanConnectDisconnectButton.setTitle("Scan & Connect", for: .normal)
        scanConnectDisconnectButton.isEnabled = true
    }
    
    private func handleConnected() {
        sendLocationButton.isHidden = false
        sendBroadcastChatButton.isHidden = false
        sendPrivateChatButton.isHidden = false
        blinkLedButton.isHidden = false
        scanConnectDisconnectButton.setTitle("Disconnect", for: .normal)
        scanConnectDisconnectButton.isEnabled = true
    }
    
    private func handleScanning() {
        sendLocationButton.isHidden = true
        sendBroadcastChatButton.isHidden = true
        sendPrivateChatButton.isHidden = true
        blinkLedButton.isHidden = true
        scanConnectDisconnectButton.setTitle("Scanning...", for: .normal)
        scanConnectDisconnectButton.isEnabled = false
    }
    
}

enum UIState {
    case scanning
    case connected
    case disconnected
}

extension Date {
    var millisecondsSinceEpoch: Int64 {
        let seconds = self.timeIntervalSince1970
        let milliseconds = seconds * 1000
        return Int64(milliseconds)
    }
}
