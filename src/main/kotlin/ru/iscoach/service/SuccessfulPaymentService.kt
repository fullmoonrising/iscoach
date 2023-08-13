package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.chatId
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.OrderRepo
import ru.iscoach.persistence.repos.PriceListRepo
import ru.iscoach.service.model.entity.OrderInfo
import ru.iscoach.service.model.OrderStatus
import ru.iscoach.service.model.ProductCategory
import ru.iscoach.service.model.entity.toEntity

@Service
class SuccessfulPaymentService(
    private val invoiceService: InvoiceService,
    private val messageService: MessageService,
    private val priceListRepo: PriceListRepo,
    private val notificationService: NotificationService,
    private val orderRepo: OrderRepo
) : KLogging() {
    fun processPayment(update: Update) {
        invoiceService.clearInvoices(update.chatId)
        val newOrder = orderRepo.save(
            OrderInfo(
                status = OrderStatus.NEW,
                payment = update.message.successfulPayment
            ).toEntity()
        ).toDto()
        if (newOrder.payload.product.category == ProductCategory.DIGITAL_GOODS) {
            priceListRepo.findItemById(newOrder.payload.product)
                ?.toDto()
                ?.let {
                    messageService.sendVoice(
                        voicePath = it.fileUrl
                            ?: throw RuntimeException("File URL not defined for product ${newOrder.payload.product}"),
                        chatId = update.chatId,
                        text = it.description
                    )
                }
                ?: throw RuntimeException("${newOrder.payload.product} is not found in price list")
            orderRepo.save(newOrder.setStatus(OrderStatus.DONE).toEntity())
        }
        notificationService.sendNotifications(newOrder)
    }

}