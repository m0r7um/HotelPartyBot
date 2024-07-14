package com.mortum.gopartybot.handler

import com.mortum.gopartybot.utils.getInlineKeyboard
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

interface CallbackHandler {

    val name: HandlerName

    fun processCallbackData(absSender: AbsSender, callbackQuery: CallbackQuery, arguments: List<String>)

    fun deleteInlineButtons(absSender: AbsSender, chatId: String, callbackQuery: CallbackQuery) {
        absSender.execute(
            EditMessageReplyMarkup(
                chatId, callbackQuery.message.messageId, callbackQuery.inlineMessageId, getInlineKeyboard(emptyList())
            )
        )
    }
}