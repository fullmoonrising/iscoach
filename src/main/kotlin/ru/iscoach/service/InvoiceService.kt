package ru.iscoach.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot
import ru.iscoach.config.BotCredentialsConfig
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.PriceListRepo
import ru.iscoach.service.model.Product
import ru.iscoach.service.model.InvoicePayload
import ru.iscoach.service.model.InvoiceProviderData
import ru.iscoach.service.model.dto.PriceListDto
import ru.iscoach.service.model.dto.toLabeledPrice

@Service
class InvoiceService(
    private val bot: ISCoachBot,
    private val botCredentials: BotCredentialsConfig,
    private val priceListRepo: PriceListRepo,
    private val objectMapper: ObjectMapper
) : KLogging() {
    fun sendInvoice(update: Update): Message? {
        return try {
            bot.execute(buildInvoice(update))
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending invoice" }
            null
        }
    }

    private fun buildInvoice(update: Update): SendInvoice {
        val product = priceListRepo.findItemById(Product.SESSION)?.toDto()
            ?: throw RuntimeException("${Product.SESSION} is not found in price list")
        val payload = buildPayload(update, product)
        val providerData = InvoiceProviderData(product)
        return SendInvoice.builder()
            .chatId(update.message.chatId)
            .title(product.id.label)
            .description("Запись на коуч сессию со мной (тут нужно опбольше текста типа после оплаты я с вами свяжусь и назначим время и тд)")
            .payload(objectMapper.writeValueAsString(payload))
            .providerToken(botCredentials.botProviderToken)
            .providerData(objectMapper.writeValueAsString(providerData))
            .currency("RUB")
            .price(product.toLabeledPrice())
            .startParameter("")
            .photoUrl("https://clevermemo.com/blog/wp-content/uploads/2019/08/complimentary-coaching-session-sample.png")
            .needName(true)
            .needPhoneNumber(true)
            .needEmail(true)
            .sendPhoneNumberToProvider(true)
            .sendEmailToProvider(true)
            .replyMarkup(buildPayButton())
            .build()
    }

    private fun buildPayload(update: Update, product: PriceListDto) =
        InvoicePayload(
            product = product.id,
            userId = update.message.from.id,
            chatId = update.message.chatId
        )

    private fun buildPayButton(): InlineKeyboardMarkup {
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