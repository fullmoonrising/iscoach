package com.telegram.folobot.controller

import com.telegram.folobot.prettyPrint
import mu.KLogging
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
class CustomErrorController() : ErrorController, KLogging() {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String? {
        logger.error { "${request.remoteUser} ${request.method} ${request.queryString}" } // TODO nice log
        return "error"
    }
}