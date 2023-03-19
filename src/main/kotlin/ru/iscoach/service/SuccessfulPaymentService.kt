package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.OrderRepo
import ru.iscoach.service.model.dto.OrderInfoDto
import ru.iscoach.service.model.OrderStatus
import ru.iscoach.service.model.dto.toEntity

@Service
class SuccessfulPaymentService(
    private val notificationService: NotificationService,
    private val orderRepo: OrderRepo
) : KLogging() {
    fun processPayment(update: Update) {
        val newOrder = orderRepo.save(
            OrderInfoDto(
                status = OrderStatus.NEW,
                payment = update.message.successfulPayment
            ).toEntity()
        )
        notificationService.sendNotifications(newOrder.toDto())
    }

}