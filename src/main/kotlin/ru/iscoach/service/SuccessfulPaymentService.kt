package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.ISCoachBot

@Service
class SuccessfulPaymentService(
    private val bot: ISCoachBot
) :KLogging() {
    fun processPayment(update: Update) {

    }

}