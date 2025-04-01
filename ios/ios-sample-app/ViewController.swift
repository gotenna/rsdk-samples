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
        
        handleDisconnected()
        
        Task {
            do {
                let initialized = try await GotennaClient.shared.initialize(sdkToken: "3a0433fe7e2671adb4cd7d87851d189f88bd23e82ab06013201642b868b02c8a", appId: "fcde", preProcessAction: nil, postProcessAction: nil, enableDebugLogs: true)
                print("initialized: \(initialized)")
            } catch {
                print("caught error initializing")
            }
        }
    }
    
    @IBAction func scanButtonTapped(_ sender: UIButton) {
        Task {
            do {
                let radios = try await GotennaClient.shared.scan(connectionType: ConnectionType.ble, address: nil)
                print("got num radios: \(radios.count)")
                self.activeRadio = radios.first
                try await self.activeRadio?.connect()
                self.handleConnected()
            } catch {
                print("caught error")
            }
        }
    }
    
    @IBAction func sendLocationButtonTapped(_ sender: UIButton) {
        Task {
            let location = SendToNetwork.Location(
                how: "h-e",
                staleTime: 60,
                lat: 35.291802,
                long: 80.846604,
                altitude: 237.21325546763973,
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
                    senderUUID: "4031c2e4-fff0-4a22-a490-1f4ae72c5c96",
                    senderCallsign: "JONAS",
                    encryptionParameters: nil,
                    uuid: ""
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
                    senderUUID: "4031c2e4-fff0-4a22-a490-1f4ae72c5c96",
                    senderCallsign: "JONAS",
                    encryptionParameters: nil,
                    uuid: ""
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
    
    private func handleDisconnected() {
        sendLocationButton.isHidden = true
        sendChatMessageButton.isHidden = true
        blinkLedButton.isHidden = true
        scanConnectDisconnectButton.setTitle("Scan & Connect", for: .normal)
    }
    
    private func handleConnected() {
        sendLocationButton.isHidden = false
        sendChatMessageButton.isHidden = false
        blinkLedButton.isHidden = false
        scanConnectDisconnectButton.setTitle("Disconnect", for: .normal)
    }
    
}

extension Date {
    var millisecondsSinceEpoch: Int64 {
        let seconds = self.timeIntervalSince1970
        let milliseconds = seconds * 1000.0
        return Int64(milliseconds)
    }
}

