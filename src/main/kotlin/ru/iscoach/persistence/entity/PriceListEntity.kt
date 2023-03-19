package ru.iscoach.persistence.entity

import jakarta.persistence.*
import ru.iscoach.service.model.Product
import ru.iscoach.service.model.dto.PriceListDto

@Entity
@Table(name = "price_list")
class PriceListEntity(
    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val id: Product,

    @Column(nullable = false)
    val amount: Int
)

fun PriceListEntity.toDto(): PriceListDto = PriceListDto(id, amount)