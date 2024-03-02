package ru.iscoach.service.handler

import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.addActionReceived
import ru.iscoach.extrensions.addCallbackCommandReceived
import ru.iscoach.extrensions.chat
import ru.iscoach.extrensions.from
import ru.iscoach.service.CallbackService
import ru.iscoach.service.InvoiceService
import ru.iscoach.service.model.type.Action
import ru.iscoach.service.model.Product

@Component
@Priority(1)
class CallbackHandler(
    private val invoiceService: InvoiceService,
    private val callbackService: CallbackService
) : Handler, KLogging() {
    override fun canHandle(update: Update) = Product.isMyCommand(update.callbackQuery?.data)
        .also { if (it) logger.addActionReceived(Action.CALLBACKCOMMAND, update.chat) }

    override fun handle(update: Update) {
            Product.fromCommand(update.callbackQuery.data)
            ?.let {
                logger.addCallbackCommandReceived(it, update.from)
                invoiceService.sendInvoice(it, update)
            }
        callbackService.answerCallbackQuery(update)
    }
}