package ru.iscoach.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import ru.iscoach.service.model.entity.Notification

@Entity
@Table(name = "notification")
class NotificationEntity(
    @Id
    @Column(nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val email: String
)

fun NotificationEntity.toDto() = Notification(userId, email)