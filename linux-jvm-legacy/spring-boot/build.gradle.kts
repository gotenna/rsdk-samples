import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "com.gotenna.sdk-examples.spring-boot"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}

repositories {
    mavenCentral()
}

extra["springShellVersion"] = "3.2.2"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.shell:spring-shell-starter")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.usb4java:usb4java-javax:1.3.0")

    implementation(libs.radioSdk)
    implementation(libs.kotlinx.serialization.protobuf.jvm)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.shell:spring-shell-dependencies:${property("springShellVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    val localProperties = gradleLocalProperties(rootDir)
    environment("SDK_TOKEN", localProperties.getProperty("sdk.token"))
    environment("SDK_APP_ID", localProperties.getProperty("sdk.app.id"))
}