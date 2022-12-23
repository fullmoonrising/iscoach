package com.telegram.folobot.service

import com.telegram.folobot.ChatId
import com.telegram.folobot.Utils
import com.telegram.folobot.model.NumTypeEnum
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val textService: TextService,
    private val messageService: MessageService,
    private val foloPidorService: FoloPidorService,
    private val userService: UserService
) : KLogging() {
    fun whatAboutIT(chatId: Long) {
        messageService.sendMessage(textService.getIT(userService.getFoloUserNameLinked(ChatId.FOLOMKIN_ID)), chatId)
    }

    fun dayStats(chatId: Long) {
        messageService.sendMessage(
            foloPidorService.getTopActive(chatId).withIndex().joinToString(
                separator = "\n",
                prefix = "*Самые активные фолопидоры дня*:\n\n",
                transform = {
                    "\u2004*${it.index + 1}*.\u2004${
                        userService.getFoloUserName(it.value, chatId)
                    } — ${Utils.getNumText(it.value.messagesPerDay, NumTypeEnum.MESSAGE)}"
                }
            ),
            chatId
        ).also { logger.info { "Sent day stats to $chatId" } }
    }
}