package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot

@Service
class CallbackService(
    private val bot: ISCoachBot
) : KLogging() {
    fun answerCallbackQuery(update: Update) {
        try {
            bot.execute(
                AnswerCallbackQuery.builder()
                    .callbackQueryId(update.callbackQuery.id)
                    .build()
            )
        } catch (ex: TelegramApiException) {
            logger.error { ex }
        }
    }

}