package ru.iscoach.service.handler

import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.addPreCheckoutQueryReceived
import ru.iscoach.service.PreCheckoutService

@Component
@Priority(2)
class PreCheckoutHandler(
    private val preCheckoutService: PreCheckoutService,
) : Handler, KLogging() {
    override fun canHandle(update: Update): Boolean {
        return update.hasPreCheckoutQuery().also { if (it) logger.addPreCheckoutQueryReceived() }
    }

    override fun handle(update: Update) {
        preCheckoutService.sendConfirmation(update)
        // TODO log update.preCheckoutQuery.orderInfo
    }
}