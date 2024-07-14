package com.mortum.gopartybot.commands

import com.mortum.gopartybot.model.CommandName
import com.mortum.gopartybot.persistance.model.Party
import com.mortum.gopartybot.service.logic.PartyService
import com.mortum.gopartybot.service.logic.UserService
import com.mortum.gopartybot.steps.Step
import com.mortum.gopartybot.utils.createMessage
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class ShowCommand(
    private val userService: UserService, private val partyService: PartyService
) : BotCommand(CommandName.SHOW.text, "") {
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        val parties: MutableList<Party> = partyService.findPartiesByFinished(false)
        if (parties.isNotEmpty()) {
            val stringBuffer = StringBuffer().append("Доступные вечеринки:\n")

            for (party in parties) {
                stringBuffer.append(party.id).append(". Вечеринка \"").append(party.name).append("\"\n")
            }

            absSender.execute(createMessage(chat.id.toString(), stringBuffer.toString()))
        } else {
            absSender.execute(createMessage(chat.id.toString(), "Доступных вечеринок пока нет :("))
        }
        userService.updateStepById(user.id, Step.COMMON_MESSAGE)
    }
}