import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
}

val sdkToken: String = gradleLocalProperties(rootDir, providers).getProperty("sdk.token")
val appId: String = gradleLocalProperties(rootDir, providers).getProperty("sdk.appid")

android {
    namespace = "com.gotenna.android.rsdksample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gotenna.android.rsdksample"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SDK_TOKEN", "\"$sdkToken\"")
        buildConfigField("String", "APP_ID", "\"$appId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.gotenna.radio.sdk)
}