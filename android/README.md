# RSDK Android Sample Project

A sample project demonstrating how to integrate and use the RSDK (Radio SDK) in an Android environment.

---

## Table of Contents

- [Overview](#overview)
- [Setup](#setup)
  - [Prerequisites](#prerequisites)
  - [Clone the Repository](#clone-the-repository)
  - [Repository & Credentials ](#repository--credentials)
- [Usage](#usage)
  - [Android Permissions](#android-permissions)
  - [Initialize](#initialize)
  - [Basic API](#basic-api)
  
---

## Overview

This project is designed to showcase the integration of RSDK in an Android application. It includes examples of initializing the SDK, handling radio connection events, and integrating UI components with radio functionalities. Whether you're a beginner or an experienced developer, this guide will help you set up and run the sample project with ease.

---

## Setup

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Meerkat | 2024.3.1 or later recommended)
- Android SDK (minimum API level 26)
- Git


### Clone the Repository

Clone the repository to your local machine.

```bash
git clone https://github.com/gotenna/rsdk-samples.git
```


### Repository & Credentials

To be able to import the RSDK, credentials must be added to the project's `local.properties`.
The credentials are provided by goTenna.

```
artifactory.user=abc@def.ghi
artifactory.password=jkl
sdk.token=mno
sdk.appid=pqr
```


In addition, the following goTenna repository must be added to a project. This has already been done for this sample project.

[settings.gradle.kts](../android/settings.gradle.kts)
```
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://gotenna.jfrog.io/artifactory/android-libs-release-local")
            credentials {
                username = [artifactoryUser]
                password = [artifactoryPassword]
            }
        }
    }
}
```

---

## Usage

### Android Permissions

Various Android run-time permissions are required by the RSDK to function. Refer to the [PermissionActivity](../android/app/src/main/java/com/gotenna/android/rsdksample/PermissionActivity.kt) for list of permissions.


### Initialize

`GotennaClient` must first be initialized. This is recommended to be done in the [Application](../android/app/src/main/java/com/gotenna/android/rsdksample/SampleApplication.kt) class.

```kotlin
GotennaClient.initialize(
    context = applicationContext,
    sdkToken = SDK_TOKEN,
    appId = APP_ID,
)
```


### Basic API

Refer to the [RadioManager](../android/app/src/main/java/com/gotenna/android/rsdksample/RadioManager.kt) class for basic calls to the RSDK API.