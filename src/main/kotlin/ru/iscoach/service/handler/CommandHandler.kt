package ru.iscoach.service.handler

import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.config.BotCredentialsConfig
import ru.iscoach.extrensions.addActionReceived
import ru.iscoach.extrensions.getBotCommand
import ru.iscoach.extrensions.isNotForward
import ru.iscoach.service.InvoiceService
import ru.iscoach.service.model.Actions
import ru.iscoach.service.model.BotCommands

@Component
@Priority(1)
class CommandHandler(
    private val invoiceService: InvoiceService,
    private val botCredentials: BotCredentialsConfig
) : Handler, KLogging() {
    fun Message?.isMyCommand() = this?.isCommand == true && this.isNotForward() && (this.chat?.isUserChat == true ||
            this.entities?.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text
                ?.contains(botCredentials.botUsername) == true)

    override fun canHandle(update: Update): Boolean {
        return (update.message?.isMyCommand() ?: false).also { if (it) logger.addActionReceived(Actions.COMMAND) }
    }

    override fun handle(update: Update) {
        when (
            BotCommands.fromCommand(update.message.getBotCommand())
                .also { logger.info { "Received command ${it ?: "UNDEFINED"}" } }
        ) {
            BotCommands.START -> invoiceService.sendInvoice(update)
            BotCommands.PAY -> invoiceService.sendInvoice(update)
            else -> {}
        }
    }
}