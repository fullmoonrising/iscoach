package com.telegram.folobot.service

import com.telegram.folobot.ChatId.Companion.FOLOMKIN_ID
import org.springframework.stereotype.Service

@Service
class ScheduleService(
    private val textService: TextService,
    private val userService: UserService,
    private val messageService: MessageService
) {
    fun whatAboutIT() {
        messageService.sendMessage(textService.getIT(userService.getFoloUserNameLinked(FOLOMKIN_ID)), -1001783789636L )
    }
}