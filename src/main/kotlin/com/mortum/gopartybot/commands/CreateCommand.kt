package com.mortum.gopartybot.commands

import com.mortum.gopartybot.handler.HandlerName
import com.mortum.gopartybot.model.CommandName
import com.mortum.gopartybot.service.logic.PartyFormService
import com.mortum.gopartybot.service.logic.PartyService
import com.mortum.gopartybot.service.logic.UserService
import com.mortum.gopartybot.steps.Step
import com.mortum.gopartybot.utils.createMessage
import com.mortum.gopartybot.utils.createMessageWithInlineButtons
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class CreateCommand(
    private val userService: UserService,
    private val partyFormService: PartyFormService,
    private val partyService: PartyService
) : BotCommand(CommandName.CREATE.text, "") {

    private val startMessage = "Введите номер, в котором вы живете:"

    private val continueMessage = "Вы уже начали создавать вечеринку. Желаете продолжить?"

    private val partyAlreadyExists = "Вы уже создали вечеринку. Вы не можете создавать больше 1 вечеринки"

    private val alreadyHasParty = "Вы не можете создать свою вечеринку, потому что уже присоединились к чужой"

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>?) {
        val userM = userService.findById(user.id).get()

        if (partyService.existsByOwner(userM)) {
            absSender.execute(createMessage(chat.id.toString(), partyAlreadyExists))
            userService.updateStepById(user.id, Step.COMMON_MESSAGE)
            return
        }

        if (userM.party != null) {
            absSender.execute(createMessage(chat.id.toString(), alreadyHasParty))
            userService.updateStepById(user.id, Step.COMMON_MESSAGE)
            return
        }

        if (!partyFormService.existsByOwner(userService.findById(user.id).get())) {
            absSender.execute(createMessage(chat.id.toString(), startMessage))
            userService.updateStepById(user.id, Step.CREATE_INPUT_NUMBER)
        } else {
            val callback = HandlerName.CONTINUE_CREATING_PARTY.text
            absSender.execute(
                createMessageWithInlineButtons(
                    chat.id.toString(),
                    continueMessage,
                    listOf(
                        listOf(
                            "$callback|y" to "Да", "$callback|n" to "Нет"
                        )
                    )
                )
            )
        }
    }
}