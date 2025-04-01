//
//  ViewController.swift
//  ios-sample-app
//
//  Created by Adam Johns on 9/25/24.
//

import UIKit
import RSDK

class ViewController: UIViewController {
    
    @IBOutlet weak var scanConnectDisconnectButton: UIButton!
    @IBOutlet weak var sendLocationButton: UIButton!
    @IBOutlet weak var sendChatMessageButton: UIButton!
    @IBOutlet weak var blinkLedButton: UIButton!
    
    private var connectedRadio: RadioModel?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        handleDisconnected()
        
        DispatchQueue.main.async {
            Task {
                do {
                    let initialized = try await GotennaClient.shared.initialize(sdkToken: "3a0433fe7e2671adb4cd7d87851d189f88bd23e82ab06013201642b868b02c8a", appId: "fcde", preProcessAction: nil, postProcessAction: nil, enableDebugLogs: true)
                    print("initialized: \(initialized)")
                } catch {
                    print("caught error initializing")
                }
            }
        }
    }
    
    @IBAction func scanButtonTapped(_ sender: UIButton) {
        DispatchQueue.main.async {
            Task {
                do {
                    let radios = try await GotennaClient.shared.scan(connectionType: ConnectionType.ble, address: nil)
                    print("got num radios: \(radios.count)")
                    self.connectedRadio = radios.first
                    let connectionResult = try await self.connectedRadio?.connect()
                    self.handleConnected()
                    
//                    let metadata = CommandMetaData(messageType: GTMessageType.broadcast, destinationGid: 0, isPeriodic: false, priority: GTMessagePriority.normal, senderGid: 904610228241489)
//                    let header = GotennaHeaderWrapper(timeStamp: 1718745135761, messageTypeWrapper: MessageTypeWrapper.location, recipientUUID: "", appCode: 0, senderGid: 904610228241489, senderUUID: "ANDROID-2440142b8ac6d5d7", senderCallsign: "JONAS", encryptionParameters: nil, uuid: "")
//                    let location = SendToNetwork.Location(how: "h-e", staleTime: 60, lat: 35.291802, long: 80.846604, altitude: 237.21325546763973, team: "CYAN", accuracy: 11, creationTime: 1718745135755, messageId: 0, commandMetaData: metadata, commandHeader: header, gripResult: GripResultUnknown(), _bytes: nil, sequenceId: -1)
//                    
//                    let pliResult = try await radio1?.send(model: location)
                } catch {
                    print("caught error")
                }
            }
        }
    }
    
    @IBAction func blinkLedButtonTapped(_ sender: UIButton) {
        Task {
            do {
                try await connectedRadio?.performLedBlink()
            } catch {
                print("Error performing LED blink: \(error)")
            }
        }
    }
    
    private func handleDisconnected() {
        sendLocationButton.isHidden = true
        sendChatMessageButton.isHidden = true
        blinkLedButton.isHidden = true
        scanConnectDisconnectButton.titleLabel?.text = "Scan & Connect"
    }
    
    private func handleConnected() {
        sendLocationButton.isHidden = false
        sendChatMessageButton.isHidden = false
        blinkLedButton.isHidden = false
        scanConnectDisconnectButton.titleLabel?.text = "Disconnect"
    }
}

