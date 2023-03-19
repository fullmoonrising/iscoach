package ru.iscoach.service.handler

import org.telegram.telegrambots.meta.api.objects.Update

interface Handler {
    fun canHandle(update: Update): Boolean
    fun handle(update: Update)
}