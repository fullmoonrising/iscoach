package com.telegram.folobot.controller

import mu.KLogging
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.File

@Controller
class IndexController() : KLogging() {
    @GetMapping(value=["", "/", "index"])
    fun index(model: MutableMap<String, Any>): String {
        model["rndvid"] = "/media/${ File("src/main/resources/static/media").list()?.random() }"
        return "index"
//        return "redirect:/frontpage";
    }
}