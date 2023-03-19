package ru.iscoach.service.model.dto

import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice
import ru.iscoach.persistence.entity.PriceListEntity
import ru.iscoach.service.model.Product

data class PriceListDto(
    val id: Product,
    val amount: Int
)

fun PriceListDto.toLabeledPrice() = LabeledPrice(id.label, amount)

fun PriceListDto.toEntity() = PriceListEntity(id, amount)