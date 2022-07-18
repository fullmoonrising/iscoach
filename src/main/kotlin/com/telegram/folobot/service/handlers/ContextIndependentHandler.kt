package com.telegram.folobot.service.handlers

import com.telegram.folobot.ChatId
import com.telegram.folobot.ChatId.Companion.isAndrew
import com.telegram.folobot.service.FoloPidorService
import com.telegram.folobot.service.FoloUserService
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.Objects

@Component
class ContextIndependentHandler(
    private val foloUserService: FoloUserService,
    private val foloPidorService: FoloPidorService,
    private val messageService: MessageService,
    private val userService: UserService
) {

    fun handle(update: Update) {
        //Добавление фолопользователя в бд
        addFoloUser(update)

        //Пересылка личных сообщений в спецчат
        forwardPrivate(update)
    }

    /**
     * Залоггировать пользователя
     *
     * @param update [Update]
     */
    private fun addFoloUser(update: Update) {
        val message = update.message
        if (message.isAutomaticForward == null || !message.isAutomaticForward) {
            var user = message.from
            if (Objects.isNull(user) && message.newChatMembers.isNotEmpty()) {
                user = message.newChatMembers[0]
            }
            if (!Objects.isNull(user)) {
                // Фолопользователь
                foloUserService.save(
                    foloUserService.findById(user.id).setName(userService.getUserName(user))
                )
                // И фолопидор
                if (!message.isUserMessage) {
                    foloPidorService.save(
                        foloPidorService.findById(message.chatId, user.id).updateMessagesPerDay()
                    )
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
        } else if (isAndrew(update.message.from)) {
            messageService.forwardMessage(ChatId.ANDREWSLEGACY_ID, update)
        }
    }
}