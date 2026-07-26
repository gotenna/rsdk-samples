package com.gotenna.spring.sample

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ApplicationListener
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.standard.ShellCommandGroup
import org.springframework.stereotype.Component

private fun versionLine(buildProperties: BuildProperties?): String {
    val version = buildProperties?.version ?: "dev"
    val buildNumber = buildProperties?.get("buildNumber") ?: "0"
    return "RSDK JVM sample v$version (build $buildNumber)"
}

// Prints the version line on startup, before the interactive shell takes over.
@Component
class VersionBanner(private val buildProperties: ObjectProvider<BuildProperties>) : ApplicationListener<ApplicationStartedEvent> {

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        println(versionLine(buildProperties.ifAvailable))
    }
}

@Command
@ShellCommandGroup("Application")
class VersionCommands(private val buildProperties: ObjectProvider<BuildProperties>) {

    @Command(
        command = ["version"],
        description = "Show the app version and build number."
    )
    fun version(): String {
        return versionLine(buildProperties.ifAvailable)
    }
}
