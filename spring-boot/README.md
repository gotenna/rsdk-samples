



## Add the SDK to your project

The Radio SDK is hosted on a private goTenna Artifactory. Setup up the repository in `build.gradle`

```gradle
repositories {
    ...
    maven {
        url = uri("https://gotenna.jfrog.io/artifactory/android-libs-release-local")
        name = "artifactory"
        credentials {
            username = "user@gotenna.com"
            password = "superSecretPassword"
        }
    }
}
```

Then include the SDK in your dependencies

```gradle
dependencies {
    ...
    implementation("com.gotenna.sdk:radio-sdk-external-jvm:$sdkVersion")
}
```

## Usage Overview

- initialize the client
  - sdk token
  - app id
  - `GotennaClient.initialize()`
- scan for radios
    - usb, ble
    - `GotennaClient.scan()`
- connect to a radio
  - `radio.connect()`
- configure the radio
  - set frequency channels `radio.setFrequencyChannels()`
  - power, bandwidth `radio.setPowerAndBandwidth()`
- listen and interact
  - collect `radio.radioEvents` and/or `radio.radioState` flows