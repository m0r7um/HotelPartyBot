package com.mortum.gopartybot.handler

import com.mortum.gopartybot.service.logic.PartyFormService
import com.mortum.gopartybot.service.logic.UserService
import com.mortum.gopartybot.steps.Step
import com.mortum.gopartybot.utils.createMessage
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class ContinueCreatingHandler(
    private val userService: UserService, private val partyFormService: PartyFormService
) : CallbackHandler {
    override val name: HandlerName = HandlerName.CONTINUE_CREATING_PARTY

    private val yesMessage: String = "Хорошо, тогда продолжим!\nВведите название вечеринки:"

    private val noMessage: String = "Ладно, тогда начнем заново!\nВведите номер отеля:"

    override fun processCallbackData(
        absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>
    ) {
        val chatId = callbackQuery.message.chatId.toString()

        deleteInlineButtons(absSender, chatId, callbackQuery)

        if (arguments.first() == "y") {
            userService.updateStepById(callbackQuery.from.id, Step.CREATE_INPUT_NAME)
            absSender.execute(createMessage(chatId, yesMessage))
        } else {
            partyFormService.deleteByUser(userService.findById(callbackQuery.from.id).get())
            userService.updateStepById(callbackQuery.from.id, Step.CREATE_INPUT_NUMBER)
            absSender.execute(createMessage(chatId, noMessage))
        }
    }
}