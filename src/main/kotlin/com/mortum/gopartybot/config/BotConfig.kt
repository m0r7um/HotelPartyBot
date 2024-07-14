package com.mortum.gopartybot.config

import com.mortum.gopartybot.service.bot.GoPartyBot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class BotConfig {
    @Bean
    fun telegramBotsApi(bot: GoPartyBot): TelegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java).apply {
        registerBot(bot)
    }
}