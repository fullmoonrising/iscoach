package ru.iscoach.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController() : KLogging() {
    @GetMapping(value = ["", "/", "/index"])
    fun index(): String {
        return "index"
    }
}