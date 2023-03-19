package ru.iscoach.service.model.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment
import ru.iscoach.persistence.entity.OrderInfoEntity
import ru.iscoach.service.model.InvoicePayload
import ru.iscoach.service.model.OrderStatus
import java.io.Serializable

val objectMapper: ObjectMapper
    get() {
        return jacksonObjectMapper()
    }

class OrderInfoDto(
    val id: Int? = null,
    val status: OrderStatus,
    val payment: SuccessfulPayment,
    val payload: InvoicePayload = objectMapper.readValue(payment.invoicePayload)
) : Serializable

fun OrderInfoDto.toEntity() = OrderInfoEntity(id, status, payment)