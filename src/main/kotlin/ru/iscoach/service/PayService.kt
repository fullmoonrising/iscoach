package ru.iscoach.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice
import org.telegram.telegrambots.meta.api.objects.Message

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot

@Service
class PayService(
    private val bot: ISCoachBot,
    private val objectMapper: ObjectMapper
) :KLogging() {
    fun sendInvoice(update: Update): Message? {
        return try {
            bot.execute(buildInvoice(update))
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending invoice" }
            null
        }
    }

    private fun buildInvoice(update: Update): SendInvoice {
        return SendInvoice.builder()
            .chatId(update.message.chatId)
            .title("Коуч сессия")
            .description("Запись на коуч сессию со мной")
            .payload(objectMapper.writeValueAsString(update.message.chat))
            .providerToken("381764678:TEST:52779")
            .currency("RUB")
            .price(LabeledPrice("Сессия", 1000*100))
            .startParameter("")
            .photoUrl("https://clevermemo.com/blog/wp-content/uploads/2019/08/complimentary-coaching-session-sample.png")
            .needName(true)
            .needPhoneNumber(true)
            .needEmail(true)
            .replyMarkup(buildPayButton())
            .build()
    }

    private fun buildPayButton(): InlineKeyboardMarkup{
        return InlineKeyboardMarkup.builder()
            .keyboardRow(
                listOf(
                    InlineKeyboardButton.builder()
                        .text("Записаться на сессию")
                        .pay(true)
                        .build()
                )
            )
            .build()
    }
}