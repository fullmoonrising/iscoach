package com.telegram.folobot.service.handlers

import com.telegram.folobot.ChatId.Companion.isAndrew
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

@Component
class ReplyHandler(
    private val userService: UserService,
    private val messageService: MessageService
) {

    /**
     * Ответ на обращение
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handle(update: Update): BotApiMethod<*>? {
        // Cообщение в чат
        val text = update.message.text.lowercase(Locale.getDefault())
        if (text.contains("привет") || SplittableRandom().nextInt(100) < 20) {
            val userName = userService.getFoloUserName(update.message.from)
            messageService.sendChatTyping(update)
            return if (isAndrew(update.message.from)) {
                messageService
                    .buildMessage("Привет, моя сладкая бориспольская булочка!", update, true)
            } else {
                messageService
                    .buildMessage("Привет, уважаемый фолофил $userName!", update, true)
            }
        }
        return null
    }
}