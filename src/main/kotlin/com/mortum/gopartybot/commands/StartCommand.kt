package com.mortum.gopartybot.commands

import com.mortum.gopartybot.model.CommandName
import com.mortum.gopartybot.service.logic.UserService
import com.mortum.gopartybot.utils.createMessage
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class StartCommand(
    private val userService: UserService
) : BotCommand(CommandName.START.text, ""), Logging {
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        val userName = "${user.firstName} ${user.lastName ?: ""}".trim()

        absSender.execute(createMessage(chat.id.toString(), "Добро пожаловать, $userName"))
        val newUser = com.mortum.gopartybot.persistance.model.User(user, chatId = chat.id)

        logger.info("Присоединился пользователь $userName")

        userService.addUser(newUser)
    }
}