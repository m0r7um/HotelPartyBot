package com.mortum.gopartybot.commands

import com.mortum.gopartybot.model.CommandName
import com.mortum.gopartybot.service.logic.UserService
import com.mortum.gopartybot.steps.Step
import com.mortum.gopartybot.utils.createMessage
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class SelectParty(
    val userService: UserService
): BotCommand(CommandName.SELECT.text, ""){
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        absSender.execute(createMessage(chat.id.toString(), "Введите айди вечеринки"))
        userService.updateStepById(user.id, Step.SELECT_INPUT_PARTY_ID)
    }
}