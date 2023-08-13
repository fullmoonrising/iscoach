package ru.iscoach.service.handler.message

import mu.KLogging
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.service.handler.Handler

abstract class AbstractMessageHandler : Handler, KLogging() {
    override fun canHandle(update: Update) = update.hasMessage()
}