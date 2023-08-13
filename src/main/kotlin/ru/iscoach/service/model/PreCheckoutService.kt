package ru.iscoach.service.model

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot
import ru.iscoach.extrensions.toObject
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.PriceListRepo

@Service
class PreCheckoutService(
    private val bot: ISCoachBot,
    private val priceListRepo: PriceListRepo
) : KLogging() {
    fun confirmOrder(update: Update) {
        val invoicePayload: InvoicePayload = update.preCheckoutQuery.invoicePayload.toObject()
        val amount = priceListRepo.findItemById(invoicePayload.product)?.toDto()?.amount
        val isValid = invoicePayload.amount == amount
        sendConfirmation(update, isValid)
    }

    private fun sendConfirmation(update: Update, isValid: Boolean): Boolean {
        return try {
            bot.execute(buildConfirmation(update, isValid))
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending pre checkout confirmation" }
            false
        }
    }

    private fun buildConfirmation(update: Update, isValid: Boolean): AnswerPreCheckoutQuery {
        val answer = AnswerPreCheckoutQuery.builder()
            .preCheckoutQueryId(update.preCheckoutQuery.id)
            .ok(isValid)
        if (!isValid) answer.errorMessage("Счет неактуален, запросите новый")
        return answer.build()
    }
}