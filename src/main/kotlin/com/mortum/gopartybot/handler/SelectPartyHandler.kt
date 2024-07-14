package com.mortum.gopartybot.handler

import com.mortum.gopartybot.service.logic.PartyService
import com.mortum.gopartybot.service.logic.UserService
import com.mortum.gopartybot.steps.Step
import com.mortum.gopartybot.utils.chooseOptimalNumber
import com.mortum.gopartybot.utils.createMessage
import com.mortum.gopartybot.utils.sendToUsers
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class SelectPartyHandler(
    private val partyService: PartyService,
    private val userService: UserService
) : CallbackHandler {
    override val name: HandlerName = HandlerName.SELECT_PARTY
    override fun processCallbackData(
        absSender: AbsSender,
        callbackQuery: CallbackQuery,
        arguments: List<String>
    ) {
        val chatId = callbackQuery.message.chatId.toString()

        deleteInlineButtons(absSender, chatId, callbackQuery)
        try {
            val partyId = arguments[1].toLong()
            val party = partyService.findById(partyId).get()

            val userId = callbackQuery.from.id
            if (!party.finished) {// проверка на то, что вечеринку могут завершить до того, как пользователь нажал кнопку
                if (arguments.first() == "finish") {

                    partyService.updatePartyFinishedById(partyId, true)

                    val newParty = partyService.findById(partyId).get()


                    // если пользователь не указал свой номер при присоединении к вечеринке, то он в ней не участвует
                    newParty.users.filter { it.number != null }

                    val users = newParty.users

                    val newList = mutableListOf<Int>()

                    for (user in users) {
                        if (user.number != null) {
                            newList.add(user.number)
                        }
                    }

                    val optimalNumber = chooseOptimalNumber(newList)

                    partyService.addParty(newParty)

                    absSender.execute(createMessage(chatId, "Вечеринка завершена"))

                    sendToUsers(
                        users = users, absSender = absSender, message = "Вечеринка №$partyId: ${party.name}\n" +
                                "Пройдет в номере $optimalNumber"
                    )

                    userService.updateStepById(userId, Step.COMMON_MESSAGE)
                } else {
                    val user = userService.findById(userId).get()

                    if (user.number == null) {
                        absSender.execute(
                            createMessage(
                                chatId = chatId,
                                text = "Вы не указали свой номер. Введите свой номер и попробуйте присоединиться еще раз:"
                            )
                        )

                        userService.updateStepById(userId, Step.SELECT_INPUT_NUMBER)
                        return
                    }

                    if (user.party == null) {
                        user.party = party
                        party.addUserToParty(user)
                        userService.updateStepById(userId, Step.COMMON_MESSAGE)
                        userService.updateUserPartyById(userId, party)
                        partyService.addParty(party)
                        absSender.execute(createMessage(chatId, "Вы присоединились к вечеринке"))
                    } else {
                        absSender.execute(
                            createMessage(
                                chatId = chatId,
                                text = "Вы не можете присоединиться к нескольким вечеринкам.\n" +
                                        "Возможно, вы являетесь создателем одной из вечеринок"
                            )
                        )
                        userService.updateStepById(userId, Step.COMMON_MESSAGE)
                    }
                }
            } else {
                absSender.execute(
                    createMessage(
                        chatId = chatId,
                        text = "Данная вечеринка уже была завершена :("
                    )
                )
            }
        } catch (e: NumberFormatException) {
            absSender.execute(createMessage(chatId, "Введенный текст не является числом. Повторите ввод"))
        }
    }

}