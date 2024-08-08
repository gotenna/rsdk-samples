package com.gotenna.sdk_examples.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.CommandScan

@SpringBootApplication
@CommandScan
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}