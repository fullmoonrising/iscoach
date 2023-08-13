package ru.iscoach.service.model

import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.chatId
import ru.iscoach.service.model.entity.PriceListItem

data class InvoicePayload(
    val product: Product,
    val amount: Int,
    val chatId: Long
) {
    constructor(product: PriceListItem, update: Update): this(product.id, product.amount, update.chatId)
}

