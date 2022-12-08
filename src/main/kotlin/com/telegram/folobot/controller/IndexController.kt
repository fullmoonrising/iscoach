package com.telegram.folobot.controller

import mu.KLogging
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class IndexController() : KLogging() {
    @GetMapping(value=["", "/", "index"])
    fun index(model: MutableMap<String, Any>): String {
        model["vid"] = ClassPathResource("static\\media\\wololo.mp4").inputStream
        return "index"

//        return "redirect:/frontpage";
    }
}