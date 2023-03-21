package ru.iscoach.service.model

import ru.iscoach.service.model.dto.PriceListDto

data class InvoicePayload(
    val product: PriceListDto,
    val userId: Long,
    val chatId: Long,
)
