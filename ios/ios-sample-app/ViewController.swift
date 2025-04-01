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
                creationTime: 1718745135755,
                messageId: 0,
                commandMetaData: CommandMetaData(
                    messageType: GTMessageType.broadcast,
                    destinationGid: 0,
                    isPeriodic: false,
                    priority: GTMessagePriority.normal,
                    senderGid: 904610228241489
                ),
                commandHeader: GotennaHeaderWrapper(
                    timeStamp: 1718745135761,
                    messageTypeWrapper: MessageTypeWrapper.location,
                    recipientUUID: "",
                    appCode: 0,
                    senderGid: 904610228241489,
                    senderUUID: "ANDROID-2440142b8ac6d5d7",
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
