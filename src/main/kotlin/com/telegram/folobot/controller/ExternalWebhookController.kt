package com.telegram.folobot.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/folopidordaily")
class ExternalWebhookController {
    @GetMapping
    fun get(): ResponseEntity<*> {
        return ResponseEntity.ok().build<Any>()
    }

    @PostMapping
    fun onRequestReceived(): ResponseEntity<*> {
        return ResponseEntity.ok().build<Any>()
    }
}