package com.telegram.folobot.controller

import com.telegram.folobot.FoloBot
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@RestController
class WebhookController(private val foloBot: FoloBot) {
    @GetMapping
    fun get(): ResponseEntity<*> {
        return ResponseEntity.ok().build<Any>()
    }

    @PostMapping
    fun onUpdateReceived(@RequestBody update: Update): BotApiMethod<*>? {
        return foloBot.onWebhookUpdateReceived(update)
    }
}