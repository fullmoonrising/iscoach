package ru.iscoach.persistence.entity

import jakarta.persistence.*
import ru.iscoach.service.model.Product
import ru.iscoach.service.model.entity.PriceListItem

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

    @Column(nullable = true)
    val photoUrl: String?,

    @Column(nullable = true)
    val fileUrl: String?,

    @Column(nullable = false)
    val order: Int
)

fun PriceListEntity.toDto(): PriceListItem = PriceListItem(id, amount, name, description, photoUrl, fileUrl, order)