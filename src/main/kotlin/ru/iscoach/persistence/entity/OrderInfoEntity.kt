package ru.iscoach.persistence.entity


import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment
import ru.iscoach.service.model.entity.OrderInfo
import ru.iscoach.service.model.type.OrderStatus

@Entity
@Table(name = "order_info")
class OrderInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val payment: SuccessfulPayment
)

fun OrderInfoEntity.toDto() = OrderInfo(id, status, payment)