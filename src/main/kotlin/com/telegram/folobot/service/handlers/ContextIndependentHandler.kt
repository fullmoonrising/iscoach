package com.telegram.folobot.service.handlers

import com.telegram.folobot.ChatId
import com.telegram.folobot.ChatId.Companion.isAndrew
import com.telegram.folobot.prettyPrint
import com.telegram.folobot.service.FoloPidorService
import com.telegram.folobot.service.FoloUserService
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.getName
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

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
            var user = message.from
            if (Objects.isNull(user) && message.newChatMembers.isNotEmpty()) {
                user = message.newChatMembers[0]
            }
            if (!Objects.isNull(user)) {
                // Фолопользователь
                val foloUser = foloUserService.findById(user.id).setName(user.getName())
                foloUserService.save(foloUser)
                logger.info { "Saved foloUser ${foloUser.prettyPrint()}" }
                // И фолопидор
                if (!message.isUserMessage) {
                    val foloPidor = foloPidorService.findById(message.chatId, user.id).updateMessagesPerDay()
                    foloPidorService.save(foloPidor)
                    logger.info { "Saved foloPidor ${foloPidor.prettyPrint()}" }
                }
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