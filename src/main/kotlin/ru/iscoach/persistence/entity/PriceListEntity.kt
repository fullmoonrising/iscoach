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
    val amount: Int,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val order: Int
)

fun PriceListEntity.toDto(): PriceListDto = PriceListDto(id, amount, name, description, order)