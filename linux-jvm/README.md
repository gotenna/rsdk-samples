# RSDK Linux JVM Sample Project

A sample project demonstrating how to integrate and use the RSDK (Radio SDK) in a Linux environment using the JVM.

---

## Table of Contents

- [Overview](#overview)
- [Setup Instructions](#setup-instructions)
- [Project Structure](#project-structure)
- [Running the Application](#running-the-application)

---

## Overview

This project showcases the integration of RSDK in a Linux JVM environment. It demonstrates how to initialize the SDK, manage radio connection events, and use its functionalities in a desktop or server-side application.

---

## Setup Instructions

### Prerequisites

- Java Development Kit (JDK 11 or later)
- Apache Maven or Gradle (depending on your project configuration)
- Git

### Clone the Repository

Clone the repository to your local machine:

```bash
git clone https://github.com/yourrepo/rsdk-sample.git
```

### Add the SDK to your project

The Radio SDK is hosted on a private goTenna Artifactory. The repository is set up in `settings.gradle.kts` and uses the credentials provided in the `local.properties` file. Provide your credentials there.
```gradle
# local.properties
artifactory.user=
artifactory.password=

# token
sdk.token=
sdk.app.id=
```

The SDK is included in the app dependencies

```gradle
// spring-boot/build.gradle.kts
dependencies {
    ...
    implementation(libs.radioSdk)
}
```

## Usage Overview

### Initialize the client

```kotlin
GotennaClient.initialize(
    sdkToken = SDK_TOKEN,
    appId = APP_ID,
)
```

### Listen for Radio Status Changes
Will let you know if the connection status of any of the connected radios changes.

```kotlin
GotennaClient.observeRadios().collect { radios ->
}
```

### Scan for Radios
Will return a list of radios found nearby.

```kotlin
val radioList = GotennaClient.scan(ConnectionType.USB)
```

### Connect to a Radio
Take the radio from the scan list and call .connect() on it.

```kotlin
val result = radio.connect()
```
---
## Communication Suggestions
What we normally do is this:

- Node A sends everyone on the mesh network its LocationModel as a broadcast message every minute.
- Node B receives LocationModel from A and other nodes.
- Node B stores (app logic) the GID of each node in a contact list.
- Node B uses the contact list to get the GID of the node they want to contact.
