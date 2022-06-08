package com.telegram.folobot

import com.telegram.folobot.constants.ActionsEnum
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.UserService
import com.telegram.folobot.service.handlers.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

@Service
class FoloBot(
    private val ciHandler: ContextIndependentHandler,
    private val commandHandler: CommandHandler,
    private val userMessageHandler: UserMessageHandler,
    private val replyHandler: ReplyHandler,
    private val userJoinHandler: UserJoinHandler,
    private val messageService: MessageService,
    private val userService: UserService
) : TelegramWebhookBot() {
    @Value("\${bot.username}")
    private val botUsername: String? = null
    @Value("\${bot.token}")
    private val botToken: String? = null
    @Value("\${bot.path}")
    private val botPath: String? = null

    /**
     * Инициализация бота в обработчиках
     */
    init {
        messageService.foloBot = this
        userService.foloBot = this
    }

    override fun getBotToken(): String? {
        return botToken
    }

    override fun getBotUsername(): String? {
        return botUsername
    }

    override fun getBotPath(): String? {
        return botPath
    }

    /**
     * Пришел update от Telegram
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? {
        if (update.hasMessage()) {
            //Выполнение независящих от контекста действий
            ciHandler.handle(update)

            //Действие в зависимости от содержимого update
            return onAction(getAction(update), update)
        }
        return null
    }

    /**
     * Определяет действие на основе приходящего Update
     *
     * @param update [Update] пробрасывается из onUpdateReceived
     * @return [ActionsEnum]
     */
    private fun getAction(update: Update): ActionsEnum {
        val message = update.message

        // Команда
        if (message.hasText()) {
            if (message.chat.isUserChat && message.text.startsWith("/") || !message.chat.isUserChat && message.text.startsWith(
                    "/"
                ) && message.text.endsWith(
                    "@$botUsername"
                )
            ) {
                return ActionsEnum.COMMAND
            }
        }
        // Личное сообщение
        if (message.isUserMessage) {
            return ActionsEnum.USERMESSAGE
        }
        // Ответ на обращение
        if (message.hasText()) {
            if (message.text.lowercase(Locale.getDefault()).contains("гурманыч") ||
                message.text.lowercase(Locale.getDefault()).contains(botUsername!!.lowercase(Locale.getDefault()))
            ) {
                return ActionsEnum.REPLY
            }
        }
        // Пользователь зашел в чат
        if (!message.newChatMembers.isEmpty()) {
            return ActionsEnum.USERNEW
        }
        // Пользователь покинул чат
        return if (Objects.nonNull(message.leftChatMember)) {
            ActionsEnum.USERLEFT
        } else ActionsEnum.UNDEFINED
    }

    /**
     * Действие в зависимости от содержимого [Update]
     *
     * @param action [ActionsEnum]
     * @param update пробрасывается из onUpdateReceived
     * @return [BotApiMethod]
     */
    private fun onAction(action: ActionsEnum?, update: Update): BotApiMethod<*>? {
        if (action != null && action !== ActionsEnum.UNDEFINED) {
            when (action) {
                ActionsEnum.COMMAND -> return commandHandler.handle(update)
                ActionsEnum.USERMESSAGE -> return userMessageHandler.handle(update)
                ActionsEnum.REPLY -> return replyHandler.handle(update) //TODO обрабатывать фолопидор, алые паруса и восточная любовь
                ActionsEnum.USERNEW -> return userJoinHandler.handleJoin(update)
                ActionsEnum.USERLEFT -> return userJoinHandler.handleLeft(update)
                else -> return null
            }
        }
        return null
    }
}