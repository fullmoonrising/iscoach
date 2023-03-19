package ru.iscoach.service.model

data class InvoicePayload(
    val product: Product,
    val userId: Long,
    val chatId: Long
)
