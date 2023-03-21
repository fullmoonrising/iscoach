package ru.iscoach.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.PriceListRepo
import ru.iscoach.service.model.InvoicePayload
import ru.iscoach.service.model.Product

@Service
class PreCheckoutService(
    private val bot: ISCoachBot,
    private val objectMapper: ObjectMapper,
    private val priceListRepo: PriceListRepo
) : KLogging() {
    fun confirmOrder(update: Update) {
        val invoicePrice: InvoicePayload = objectMapper.readValue(update.preCheckoutQuery.invoicePayload)
        val amount = priceListRepo.findItemById(Product.SESSION)?.toDto()?.amount
        val isValid = invoicePrice.product.amount == amount
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