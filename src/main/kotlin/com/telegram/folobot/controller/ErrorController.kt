package com.telegram.folobot.controller

import mu.KLogging
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.RequestDispatcher.ERROR_REQUEST_URI
import javax.servlet.RequestDispatcher.ERROR_STATUS_CODE
import javax.servlet.http.HttpServletRequest

@Controller
class CustomErrorController() : ErrorController, KLogging() {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String? {
        request.parameterMap.flatMap { (key, values) -> values.associateBy { key }.toList() }
        logger.trace { "Bad request:\n" +
                "   method ${request.method}\n" +
                "   status: ${request.getAttribute(ERROR_STATUS_CODE)}\n" +
                "   URI: ${request.getAttribute(ERROR_REQUEST_URI)}\n" +
                "   header: ${request.headerNames.toList().map { it to request.getHeader(it) }}\n" +
                "   params: ${request.parameterMap.flatMap { (key, value) -> value.map { key to it } }}\n" +
                "   body ${request.reader.readLines()}"
        }
        return "error"
    }
}