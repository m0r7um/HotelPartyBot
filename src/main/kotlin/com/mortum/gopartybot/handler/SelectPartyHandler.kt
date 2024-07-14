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

    private val NUMBER_NOT_PRESENT_MESSAGE = "Вы не указали свой номер. Введите свой номер и нажмите присоединиться еще раз:"

    private val CONNECTED_TO_PARTY_MESSAGE = "Вы присоединились к вечеринке"

    private val CANT_CONNECT_TO_MULTIPLE_PARTIES_MESSAGE = "Вы не можете присоединиться к нескольким вечеринкам.\n" +
                                                            "Возможно, вы являетесь создателем одной из вечеринок"

    private val PARTY_IS_OVER_CONNECT_MESSAGE = "Данная вечеринка уже была завершена :("

    private val INCORRECT_NUMBER_FORMAT_MESSAGE = "Введенный текст не является числом. Повторите ввод:"

    private val PARTY_IS_OVER_FINISH_MESSAGE = "Вечеринка завершена"
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

                    absSender.execute(createMessage(chatId, PARTY_IS_OVER_FINISH_MESSAGE))

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
                                text = NUMBER_NOT_PRESENT_MESSAGE
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
                        absSender.execute(createMessage(chatId, CONNECTED_TO_PARTY_MESSAGE))
                    } else {
                        absSender.execute(
                            createMessage(
                                chatId = chatId,
                                text = CANT_CONNECT_TO_MULTIPLE_PARTIES_MESSAGE
                            )
                        )
                        userService.updateStepById(userId, Step.COMMON_MESSAGE)
                    }
                }
            } else {
                absSender.execute(
                    createMessage(
                        chatId = chatId,
                        text = PARTY_IS_OVER_CONNECT_MESSAGE
                    )
                )
            }
        } catch (e: NumberFormatException) {
            absSender.execute(createMessage(chatId, INCORRECT_NUMBER_FORMAT_MESSAGE))
        }
    }

}