# RSDK - Radio SDK
[![Version](https://img.shields.io/badge/Version-3.4.15-blue)](https://github.com/gotenna/rsdk-samples/)
[![Test Coverage](https://img.shields.io/badge/Coverage-86.55%25-brightgreen)](https://ci.example.com/testcoverage)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)](https://ci.example.com/buildstatus)

RSDK is a robust and efficient SDK designed to handle everything related to radio connections. This sample project demonstrates its capabilities across multiple platforms.

---

## Table of Contents
- [Overview](#overview)
- [Packages](#packages)
  - [Android Sample](#android-sample)
  - [iOS Sample](#ios-sample)
  - [Linux JVM Sample](#linux-jvm-sample)
- [Supported Features](#supported-features)
- [RadioModel API Reference](#radiomodel-api-reference)
- [Test Coverage & Results](#test-coverage--results)
- [Obtaining your own SDK token](#obtaining-your-own-sdk-token)

---

## Overview

The Radio SDK is designed to simplify development by giving clients the guardrails and abstractions they need — so instead of worrying about how the radio works, they can focus entirely on building great features.

RSDK provides a seamless, reliable way to manage radio connections across Android, iOS, and Linux JVM platforms. Its modular architecture and extensive test coverage ensure your applications stay stable and performant, no matter the integration.

---

## Packages

### Android Sample
A sample app demonstrating the integration of RSDK on Android devices.  
*Explore how to integrate radio connection features in your Android applications.*

### iOS Sample
A complete sample project showcasing the capabilities of RSDK on iOS devices.  
*Learn how to seamlessly incorporate radio connectivity into your iOS apps.*

### Linux JVM Sample
A demonstration project for using RSDK in a Linux environment via the JVM.  
*Discover the versatility of RSDK in server-side or desktop applications.*

---

## Supported Features

RSDK delivers a rich set of capabilities to help you build reliable and feature-rich applications across platforms:

### Core Radio Management
- Automatic radio discovery over USB and BLE
- Seamless connection and disconnection handling
- Background polling and health checks to ensure stable communication

### Command & Data Exchange
- Bidirectional command handling between app and radio
- Support for commands such as:
  - GRiP (large data transfer) commands
  - Setting the power and bandwidth
  - Setting the frequency
  - Flashing the LED

### Firmware Integration
- Firmware update with progress tracking
- Validation of minimum firmware version support based on radio type

### Security
Encryption is supported by the RSDK, but not **implemented** by the RSDK. Client applications can apply their own algorithms, rules, conditions, or scenarios for handling the encryption and decryption of data.

### Developer-Friendly Tools
- Modular API surface for integrating only what you need
- Extensive test coverage with unit, integration, and system tests
- Detailed logging for debugging and analytics
- Detailed API docs integrated into the IDE when you pull down the dependency

---

## Feature Reference

### Scan for Radios
- Scan for radios over USB or BLE

### Connection Management
- Connect to a radio with optional LED flash and transmitter configuration on connect
- Disconnect from a radio
- Get a deferred connect operation that can be cancelled

### Configuration
- Get and set network configuration (power, bandwidth, frequency channels) in a single request
- Get and set network mode (listen-only or normal)
- Get and set operation mode for the radio

### Radio Info
- Fetch the latest device info from hardware or retrieve cached device info
- Get the current chipset of the connected radio
- Set the SDK token for the radio (auto-performed on connect)

### GID Management
- Add a mesh network channel GID to the radio
- Remove a mesh network channel GID from the radio

### LED Control
- Get and set the LED state (enabled/disabled)
- Trigger a 3-blink LED test to identify the physical device

### Tether Mode
- Get and set tether mode with a configurable battery threshold

### Firmware
- Flash new firmware to the radio with version validation and configurable timeout
- Get a deferred firmware update operation that can be cancelled
- Cancel an ongoing file/GRiP transfer

### Observability
- Observe radio events via a shared flow of `RadioResult<RadioCommand>`
- Observe radio state changes via a state flow
- Monitor connected mesh contacts via a shared flow, purged on disconnect
- Stream log messages via a shared flow

### Messaging
- Send local requests to the radio (processed on-device)
- Send messages over the mesh network with delivery status results

---

## Obtaining your own SDK token
The code in this repository only contains samples for demonstration purposes.  
To use the RSDK in your own applications, you will need to obtain an SDK token from goTenna by contacting [prosupport@gotenna.com](mailto:prosupport@gotenna.com).
