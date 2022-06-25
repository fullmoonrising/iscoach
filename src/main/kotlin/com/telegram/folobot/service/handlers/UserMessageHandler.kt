package com.telegram.folobot.service.handlers

import com.telegram.folobot.ChatId
import com.telegram.folobot.ChatId.Companion.isAndrew
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.TextService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.random.Random

@Component
class UserMessageHandler(
    private val messageService: MessageService,
    private val textService: TextService
    ) {

    /**
     * Ответ на личное сообщение
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handle(update: Update): BotApiMethod<*>? {
        if (isAndrew(update.message.from) &&
            Random(System.nanoTime()).nextInt(100) < 7
        ) {
            messageService.forwardMessage(
                ChatId.POC_ID,
                messageService.sendMessage(textService.quoteforAndrew, update, true)
            )
        }
        return null
    }
}