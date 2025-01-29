package ru.iscoach.service.handler.message

import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.addSuccessfulPaymentReceived
import ru.iscoach.extrensions.chat
import ru.iscoach.extrensions.from
import ru.iscoach.extrensions.isSuccessfulPayment
import ru.iscoach.service.SuccessfulPaymentService

@Component
@Priority(3)
class SuccessfulPaymentHandler(
    private val successfulPaymentService: SuccessfulPaymentService
) : AbstractMessageHandler() {
    override fun canHandle(update: Update): Boolean = (super.canHandle(update) && update.message.isSuccessfulPayment())
        .also { if (it) logger.addSuccessfulPaymentReceived(update.from, update.chat) }

    override fun handle(update: Update) {
        successfulPaymentService.processPayment(update)
    }
}