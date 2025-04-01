import UIKit
import RSDK

@MainActor
class ViewController: UIViewController {
    
    @IBOutlet weak var scanConnectDisconnectButton: UIButton!
    @IBOutlet weak var sendLocationButton: UIButton!
    @IBOutlet weak var sendChatMessageButton: UIButton!
    @IBOutlet weak var blinkLedButton: UIButton!
    
    private var activeRadio: RadioModel?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        updateUIState(.disconnected)
        
        Task {
            do {
                let initialized = try await GotennaClient.shared.initialize(
                    sdkToken: "3a0433fe7e2671adb4cd7d87851d189f88bd23e82ab06013201642b868b02c8a",
                    appId: "fcde",
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
                updateUIState(.scanning)
                let radios = try await GotennaClient.shared.scan(connectionType: ConnectionType.ble, address: nil)
                self.activeRadio = radios.first
                try await self.activeRadio?.connect()
                self.updateUIState(.connected)
                startObservingRadioState()
            } catch {
                print("Error scanning/connecting: \(error)")
            }
        }
    }
    
    @IBAction func sendLocationButtonTapped(_ sender: UIButton) {
        Task {
            let location = SendToNetwork.Location(
                how: "m-g",
                staleTime: 60,
                lat: 35.291802,
                long: 80.846604,
                altitude: 0.0,
                team: "CYAN",
                accuracy: 11,
                creationTime: Date().millisecondsSinceEpoch,
                messageId: 0,
                commandMetaData: CommandMetaData(
                    messageType: GTMessageType.broadcast,
                    destinationGid: 0,
                    isPeriodic: false,
                    priority: GTMessagePriority.normal,
                    senderGid: activeRadio?.personalGid ?? 0
                ),
                commandHeader: GotennaHeaderWrapper(
                    timeStamp: Date().millisecondsSinceEpoch,
                    messageTypeWrapper: MessageTypeWrapper.location,
                    recipientUUID: "",
                    appCode: 0,
                    senderGid: activeRadio?.personalGid ?? 0,
                    senderUUID: UUID().uuidString,
                    senderCallsign: "JONAS",
                    encryptionParameters: nil,
                    uuid: UUID().uuidString
                ),
                gripResult: GripResultUnknown(),
                _bytes: nil,
                sequenceId: -1
            )

             try await activeRadio?.send(model: location)
        }
    }
    
    @IBAction func sendChatMessageButtonTapped(_ sender: UIButton) {
        Task {
            let chatMessage = SendToNetwork.ChatMessage(
                text: "hello world",
                chatId: 1234,
                chatMessageId: "msgId",
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
                    senderUUID: UUID().uuidString,
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
            try await activeRadio?.radioState.collect(collector: Collector<RadioState>(callback: { newState in
                print("Radio state changed to: \(newState)")
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
        sendChatMessageButton.isHidden = true
        blinkLedButton.isHidden = true
        scanConnectDisconnectButton.setTitle("Scan & Connect", for: .normal)
        scanConnectDisconnectButton.isEnabled = true
    }
    
    private func handleConnected() {
        sendLocationButton.isHidden = false
        sendChatMessageButton.isHidden = false
        blinkLedButton.isHidden = false
        scanConnectDisconnectButton.setTitle("Disconnect", for: .normal)
        scanConnectDisconnectButton.isEnabled = true
    }
    
    private func handleScanning() {
        sendLocationButton.isHidden = true
        sendChatMessageButton.isHidden = true
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
