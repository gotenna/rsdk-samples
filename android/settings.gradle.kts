pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    val localProps = File(rootDir, "local.properties").inputStream().use {
        java.util.Properties().apply { load(it) }
    }
    val artifactoryUser = localProps.getValue("artifactory.user") as String
    val artifactoryPassword = localProps.getValue("artifactory.password") as String

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://gotenna.jfrog.io/artifactory/android-libs-release-local")
            credentials {
                username = artifactoryUser
                password = artifactoryPassword
            }
        }
    }
}

rootProject.name = "goTenna Sample App"
include(":app")
 