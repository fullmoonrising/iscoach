package com.telegram.folobot.service.handlers

import com.telegram.folobot.ChatId
import com.telegram.folobot.ChatId.Companion.isAndrew
import com.telegram.folobot.service.FoloPidorService
import com.telegram.folobot.service.FoloUserService
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.getName
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ContextIndependentHandler(
    private val foloUserService: FoloUserService,
    private val foloPidorService: FoloPidorService,
    private val messageService: MessageService
) : KLogging() {

    fun handle(update: Update) {
        //Добавление фолопользователя в бд
        saveFoloUser(update)

        //Пересылка личных сообщений в спецчат
        forwardPrivate(update)
    }

    /**
     * Залоггировать пользователя
     *
     * @param update [Update]
     */
    private fun saveFoloUser(update: Update) {
        val message = update.message
        if (message.isAutomaticForward == null || !message.isAutomaticForward) {
            (message.from ?: message.newChatMembers?.firstOrNull())?.run {
                // Фолопользователь
                foloUserService.save(foloUserService.findById(this.id).setName(this.getName()))
                // И фолопидор
                if (!message.isUserMessage) {
                    foloPidorService.save(foloPidorService.findById(message.chatId, this.id).updateMessagesPerDay())
                }
                logger.info { "Saved foloUser ${this.getName()}" }
            }
        }
    }

    /**
     * Пересылка личных сообщений
     *
     * @param update [Update]
     */
    private fun forwardPrivate(update: Update) {
        if (update.hasMessage()) if (update.message.isUserMessage) {
            messageService.forwardMessage(ChatId.POC_ID, update)
            logger.info { "Forwarded message to POC" }
        } else if (isAndrew(update.message.from)) {
            messageService.forwardMessage(ChatId.ANDREWSLEGACY_ID, update)
            logger.info { "Forwarded message to Andrews legacy" }
        }
    }
}