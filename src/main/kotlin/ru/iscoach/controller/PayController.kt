package ru.iscoach.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PayController() : KLogging() {
    @GetMapping("/pay")
    fun index(): String {
        return "pay"
    }
}