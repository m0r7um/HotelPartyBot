package com.mortum.gopartybot.service.bot

import com.mortum.gopartybot.handler.CallbackHandler
import com.mortum.gopartybot.handler.HandlerName
import com.mortum.gopartybot.persistance.model.Party
import com.mortum.gopartybot.persistance.model.PartyForm
import com.mortum.gopartybot.service.logic.PartyFormService
import com.mortum.gopartybot.service.logic.PartyService
import com.mortum.gopartybot.service.logic.UserService
import com.mortum.gopartybot.steps.Step
import com.mortum.gopartybot.utils.createMessage
import com.mortum.gopartybot.utils.createMessageWithInlineButtons
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class GoPartyBot(
    commands: Set<BotCommand>,
    callbackHandlers: Set<CallbackHandler>,
    @Value("\${telegram.token}")
    token: String,
    private val userService: UserService,
    private val partyFormService: PartyFormService,
    private val partyService: PartyService
) : TelegramLongPollingCommandBot(token) {

    private lateinit var handlerMapping: Map<String, CallbackHandler>

    init {
        registerAll(*commands.toTypedArray())
        handlerMapping = callbackHandlers.associateBy { it.name.text }
    }

    @Value("\${telegram.botName}")
    private val botName: String = ""

    override fun getBotUsername(): String = botName

    override fun filter(message: Message?): Boolean {
        if (message != null && message.isUserMessage && message.chat.isUserChat) {
            return if (message.text.trim() != "/start") {
                !userService.existsById(message.from.id)
            } else false
        }
        return true
    }

    override fun processNonCommandUpdate(update: Update) {
        if (update.hasMessage()) {
            val userId = update.message.from.id
            if (userService.existsById(userId)) {
                userService.findById(userId).ifPresent {

                    when (it.step) {
                        Step.COMMON_MESSAGE -> {
                            onCommonMessage(update)
                        }

                        Step.CREATE_INPUT_NUMBER -> {
                            onCreateInputNumber(update)
                        }

                        Step.CREATE_INPUT_NAME -> {
                            onCreateInputName(update)
                        }

                        Step.SELECT_INPUT_PARTY_ID -> {
                            onSelectInputPartyId(update)
                        }

                        Step.SELECT_INPUT_NUMBER -> {
                            onSelectInputNumber(update)
                        }
                    }

                }
            }

        } else if (update.hasCallbackQuery()) {
            val callbackQuery = update.callbackQuery
            val callbackData = callbackQuery.data

            val callbackQueryId = callbackQuery.id
            execute(AnswerCallbackQuery(callbackQueryId))

            val callbackArguments = callbackData.split("|")
            val callbackHandlerName = callbackArguments.first()

            handlerMapping.getValue(callbackHandlerName)
                .processCallbackData(
                    this,
                    callbackQuery,
                    callbackArguments.subList(1, callbackArguments.size)
                )
        }
    }

    private fun onCommonMessage(update: Update) {
        if (update.message.hasText()) {
            execute(createMessage(update.message.chatId.toString(), "Вы написали: *${update.message.text}*"))
        }
    }

    private fun onCreateInputName(update: Update) {
        val user = userService.findById(update.message.from.id).get()
        val partyForm = partyFormService.findByUser(user).get()
        val newParty = Party(partyForm.id, partyForm.owner, update.message.text.trim())
        newParty.addUserToParty(partyForm.owner)
        newParty.owner = user

        partyService.addParty(newParty)

        userService.updateUserPartyById(user.id, newParty)

        execute(createMessage(update.message.chatId.toString(), "Ваша вечеринка добавлена успешно!"))

        userService.updateStepById(update.message.from.id, Step.COMMON_MESSAGE)

        partyFormService.deleteByUser(user)
    }

    fun onCreateInputNumber(update: Update) {
        try {
            val id = update.message.from.id
            val number = update.message.text.trim().toInt()
            val user = userService.findById(id).get()

            userService.updateUserNumberById(id, number)

            execute(createMessage(update.message.chatId.toString(), "Введите название вечеринки:"))

            partyFormService.addPartyForm(PartyForm(owner = user))

            userService.updateStepById(id, Step.CREATE_INPUT_NAME)
        } catch (e: NumberFormatException) {
            execute(
                createMessage(
                    update.message.chatId.toString(),
                    "Введенный текст не является числом. Введите ваш номер:"
                )
            )
        } catch (e: DataIntegrityViolationException) {
            execute(
                createMessage(
                    update.message.chatId.toString(),
                    "В данном номере уже кто-то живет. Введите другой номер:"
                )
            )
        }
    }

    fun onSelectInputPartyId(update: Update) {
        try {
            val partyId = update.message.text.toLong()
            val userId = update.message.from.id

            val party = partyService.findById(partyId)

            party.ifPresentOrElse(
                // если вечеринка существует
                {
                    if (it.finished) {
                        execute(createMessage(update.message.chatId.toString(), "Данная вечеринка уже завершена"))
                        userService.updateStepById(userId, Step.COMMON_MESSAGE)
                        return@ifPresentOrElse
                    }
                    val messageText = "Вечеринка №$partyId: \"${it.name}\"\nЧеловек зарегистрировано: ${it.users.size}"

                    if (it.owner?.id == userId) { // если пользователь овнер вечеринки, то у него будет кнопка завершить
                        val callback = HandlerName.SELECT_PARTY.text
                        execute(
                            createMessageWithInlineButtons(
                                chatId = update.message.chatId.toString(),
                                text = messageText,
                                listOf(
                                    listOf(
                                        "$callback|finish|$partyId" to "Завершить"
                                    )
                                )
                            )
                        )
                    } else { // если пользователь не овнер, то будет кнопка присоединиться
                        val callback = HandlerName.SELECT_PARTY.text
                        execute(
                            createMessageWithInlineButtons(
                                update.message.chatId.toString(),
                                messageText,
                                listOf(
                                    listOf(
                                        "$callback|connect|$partyId" to "Присоединиться"
                                    )
                                )
                            )
                        )
                    }
                },

                // если вечеринка не существует
                {
                    execute(
                        createMessage(
                            chatId = update.message.chatId.toString(),
                            text = "Вечеринки с данным айди не существует.\n" +
                                    "Возможно она уже была завершена другим пользователем"
                        )
                    )
                }
            )
        } catch (e: NumberFormatException) {
            execute(
                createMessage(
                    chatId = update.message.chatId.toString(),
                    "Введенный текст не является целым числом"
                )
            )
        }
    }

    fun onSelectInputNumber(update: Update) {
        try {
            val id = update.message.from.id
            val number = update.message.text.trim().toInt()

            userService.updateUserNumberById(id, number)

            execute(createMessage(update.message.chatId.toString(), "Вы указали свой номер"))
            userService.updateStepById(id, Step.COMMON_MESSAGE)
        } catch (e: NumberFormatException) {
            execute(
                createMessage(
                    update.message.chatId.toString(),
                    "Введенный текст не является числом. Введите ваш номер:"
                )
            )
        } catch (e: DataIntegrityViolationException) {
            execute(
                createMessage(
                    update.message.chatId.toString(),
                    "В данном номере уже кто-то живет. Введите другой номер:"
                )
            )
        }
    }
}