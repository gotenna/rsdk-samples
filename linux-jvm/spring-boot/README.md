



## Add the SDK to your project

The Radio SDK is hosted on a private goTenna Artifactory. The repository is set up in `settings.gradle.kts` and uses the credentials provided in the `local.properties` file. Provide your credentials there.

```
# local.properties
artifactory_user=<your username>
artifactory_password=<your password>
sdk_token=<your token>
sdk_app_id=<your app id>
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
