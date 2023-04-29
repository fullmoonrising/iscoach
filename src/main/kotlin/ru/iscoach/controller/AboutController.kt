package ru.iscoach.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AboutController() : KLogging() {
    @GetMapping("/about")
    fun about(): String {
        return "about"
    }
}