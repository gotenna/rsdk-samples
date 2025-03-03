@file:Suppress("UnstableApiUsage")

rootProject.name = "linux-jvm"
include(":spring-boot")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

val localProperties: java.util.Properties by lazy {
    File(rootDir.absolutePath, "local.properties").inputStream().use { input ->
        java.util.Properties().apply {
            load(input)
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    val artifactoryContextUrl = extra["artifactory.contextUrl"]
    val artifactoryDevRepoKey = extra["artifactory.repokey.dev"]
    val artifactoryUser = localProperties.getProperty("artifactory.user")
    val artifactoryPassword = localProperties.getProperty("artifactory.password")

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("${artifactoryContextUrl}/${artifactoryDevRepoKey}")
            credentials {
                username =  artifactoryUser
                password = artifactoryPassword
            }
        }
    }
}