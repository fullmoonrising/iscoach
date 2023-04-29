package ru.iscoach.service.model.dto

import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice
import ru.iscoach.extrensions.format
import ru.iscoach.persistence.entity.PriceListEntity
import ru.iscoach.service.model.Product

data class PriceListDto(
    val id: Product,
    val amount: Int,
    val name: String,
    val description: String,
    val order: Int,
    val doubleAmount: Double = amount / 100.0,
    val prettyAmount: String = "${doubleAmount.format()} ₽"
)

fun PriceListDto.toLabeledPrice() = LabeledPrice(id.label, amount)

fun PriceListDto.toEntity() = PriceListEntity(id, amount, name, description, order)