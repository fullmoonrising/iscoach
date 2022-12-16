package com.telegram.folobot.controller

import com.telegram.folobot.service.MessageService
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/main")
class MainController(private val messageService: MessageService) : KLogging() {
    @GetMapping
    fun main(): String {
        return "main"
    }

    @PostMapping
    fun sendMessage(@RequestParam chatId: Long, @RequestParam message: String): String {
        messageService.sendMessage(message, chatId)
        return "main"
    }
}