package ru.iscoach.service.model.entity

import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice
import ru.iscoach.extrensions.format
import ru.iscoach.persistence.entity.PriceListEntity
import ru.iscoach.service.model.Product

data class PriceListItem(
    val id: Product,
    val amount: Int,
    val name: String,
    val description: String,
    val shortDescription: String?,
    val photoUrl: String?,
    val fileUrl: String?,
    val order: Int,
    val doubleAmount: Double = amount / 100.0,
    val prettyAmount: String = "${doubleAmount.format()} ₽",
)

fun PriceListItem.toLabeledPrice(): LabeledPrice = LabeledPrice(name, amount)

fun PriceListItem.toEntity(): PriceListEntity =
    PriceListEntity(id, amount, name, description, shortDescription, photoUrl, fileUrl, order)
