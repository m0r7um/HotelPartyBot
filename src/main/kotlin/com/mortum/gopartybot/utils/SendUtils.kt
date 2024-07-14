package com.mortum.gopartybot.utils

import com.mortum.gopartybot.persistance.model.User
import org.telegram.telegrambots.meta.bots.AbsSender

fun sendToUsers(absSender: AbsSender, message: String, users: MutableSet<User>) {
    for (user in users) {
        absSender.execute(createMessage(user.chatId.toString(), message))
    }
}