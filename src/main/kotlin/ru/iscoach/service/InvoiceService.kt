package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot
import ru.iscoach.config.BotCredentialsConfig
import ru.iscoach.extrensions.chatId
import ru.iscoach.extrensions.toJson
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.PriceListRepo
import ru.iscoach.service.model.InvoicePayload
import ru.iscoach.service.model.Product
import ru.iscoach.service.model.ProductCategory
import ru.iscoach.service.model.entity.toLabeledPrice

@Service
class InvoiceService(
    private val bot: ISCoachBot,
    private val botCredentials: BotCredentialsConfig,
    private val priceListRepo: PriceListRepo,
    private val messageService: MessageService
) : KLogging() {
    private val issuedInvoices: MutableList<Message> = mutableListOf()

    fun clearInvoices(chatId: Long) {
        issuedInvoices.filter { it.chatId == chatId }.forEach { messageService.deleteMessage(it.chatId, it.messageId) }
        issuedInvoices.removeIf { it.chatId == chatId }
    }

    fun sendInvoice(product: Product, update: Update): Message? {
        clearInvoices(update.chatId)
        return try {
            bot.execute(buildInvoice(product, update)).also {
                issuedInvoices.add(it)
            }
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending invoice" }
            null
        }
    }

    private fun buildInvoice(product: Product, update: Update): SendInvoice {
        val productDetails = priceListRepo.findItemById(product)?.toDto()
            ?: throw RuntimeException("$product is not found in price list")
        val payload = InvoicePayload(productDetails, update)
        return SendInvoice.builder()
            .chatId(update.chatId)
            .title(productDetails.id.label)
            .description(productDetails.shortDescription ?: productDetails.description)
            .payload(payload.toJson())
            .providerToken(botCredentials.botProviderToken)
            .currency("RUB")
            .price(productDetails.toLabeledPrice())
            .startParameter("")
            .also { invoiceBuilder -> productDetails.photoUrl?.let { invoiceBuilder.photoUrl(it) } }
            .also {
                if (product.category == ProductCategory.SERVICE) {
                    it.needName(true)
                        .needPhoneNumber(true)
                        .needEmail(true)
                        .sendPhoneNumberToProvider(true)
                        .sendEmailToProvider(true)
                }
            }
            .build()
    }
}