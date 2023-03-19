package ru.iscoach.service.handler

import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.addPreCheckoutQueryReceived
import ru.iscoach.extrensions.addSuccessfulPaymentReceived
import ru.iscoach.service.SuccessfulPaymentService

@Component
@Priority(3)
class SuccessfulPaymentHandler(
    private val successfulPaymentService: SuccessfulPaymentService
) : Handler, KLogging() {
    override fun canHandle(update: Update): Boolean {
        return (update.message.successfulPayment != null).also { if (it) logger.addSuccessfulPaymentReceived() }
    }

    override fun handle(update: Update) {
        successfulPaymentService.processPayment(update)
    }
}