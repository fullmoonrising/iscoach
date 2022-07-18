package com.telegram.folobot.controller

import com.telegram.folobot.FoloBot
import com.telegram.folobot.constants.BotCommandsEnum
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/folopidordaily")
class ExternalWebhookController(private val foloBot: FoloBot) {
    @GetMapping
    fun get(): ResponseEntity<*> {
        return ResponseEntity.ok().build<Any>()
    }

    @PostMapping
    fun onRequestReceived() {
        return foloBot.onExternalWebhookReceived(BotCommandsEnum.FOLOPIDORDAILY)
    }
}