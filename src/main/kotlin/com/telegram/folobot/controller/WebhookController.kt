package com.telegram.folobot.controller

import com.telegram.folobot.service.FoloBot
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@RestController
@RequestMapping("/telegram-hook")
@PreAuthorize("permitAll()")
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