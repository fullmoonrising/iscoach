package ru.iscoach.service.model.entity

import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment
import ru.iscoach.extrensions.toObject
import ru.iscoach.persistence.entity.OrderInfoEntity
import ru.iscoach.service.model.InvoicePayload
import ru.iscoach.service.model.type.OrderStatus

class OrderInfo(
    val id: Int? = null,
    var status: OrderStatus,
    val payment: SuccessfulPayment,
    val payload: InvoicePayload = payment.invoicePayload.toObject()
){
    fun setStatus(status: OrderStatus): OrderInfo = this.also { it.status = status }
}

fun OrderInfo.toEntity() = OrderInfoEntity(id, status, payment)