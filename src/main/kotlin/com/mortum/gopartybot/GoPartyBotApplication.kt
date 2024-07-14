package com.mortum.gopartybot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GoPartyBotApplication

fun main(args: Array<String>) {
    runApplication<GoPartyBotApplication>(*args)
}
