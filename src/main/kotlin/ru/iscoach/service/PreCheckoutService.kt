package ru.iscoach.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot

@Service
class PreCheckoutService(
    private val bot: ISCoachBot
) :KLogging() {
    fun sendConfirmation(update: Update): Boolean {
        return try {
            bot.execute(buildConfirmation(update))
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending pre checkout confirmation" }
            false
        }
    }

    private fun buildConfirmation(update: Update): AnswerPreCheckoutQuery {
        return AnswerPreCheckoutQuery.builder()
            .preCheckoutQueryId(update.preCheckoutQuery.id)
            .ok(true)
            .build()
    }
}