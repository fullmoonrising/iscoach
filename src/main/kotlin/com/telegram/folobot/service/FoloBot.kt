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
    private val botUsername: String = ""

    @Value("\${bot.token}")
    private val botToken: String = ""

    @Value("\${bot.path}")
    private val botPath: String = ""

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
        return when {
            // Команда
            message.hasText() &&
                    ((message.chat.isUserChat && message.text.startsWith("/")) ||
                            (!message.chat.isUserChat && message.text.startsWith("/") &&
                                    message.text.endsWith("@$botUsername"))) -> ActionsEnum.COMMAND
            // Личное сообщение
            message.isUserMessage -> ActionsEnum.USERMESSAGE
            // Ответ на обращение
            message.hasText() && (message.text.lowercase().contains("гурманыч") ||
                    message.text.lowercase().contains(botUsername.lowercase())) -> ActionsEnum.REPLY
            // Пользователь зашел в чат
            message.newChatMembers.isNotEmpty() -> ActionsEnum.USERNEW
            // Пользователь покинул чат
            Objects.nonNull(message.leftChatMember) -> ActionsEnum.USERLEFT
            // Неопределено
            else -> ActionsEnum.UNDEFINED
        }
    }

    /**
     * Действие в зависимости от содержимого [Update]
     *
     * @param action [ActionsEnum]
     * @param update пробрасывается из onUpdateReceived
     * @return [BotApiMethod]
     */
    private fun onAction(action: ActionsEnum?, update: Update): BotApiMethod<*>? {
        if (action?.let { it !== ActionsEnum.UNDEFINED } == true) {
            return when (action) {
                ActionsEnum.COMMAND -> commandHandler.handle(update)
                ActionsEnum.USERMESSAGE -> userMessageHandler.handle(update)
                ActionsEnum.REPLY -> replyHandler.handle(update) //TODO обрабатывать фолопидор, алые паруса и восточная любовь
                ActionsEnum.USERNEW -> userJoinHandler.handleJoin(update)
                ActionsEnum.USERLEFT -> userJoinHandler.handleLeft(update)
                else -> null
            }
        }
        return null
    }
}