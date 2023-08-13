package ru.iscoach.service.model.entity

import ru.iscoach.persistence.entity.NotificationEntity

data class Notification(
    val userId: Long,
    val email: String
)

fun Notification.toEntity() = NotificationEntity(userId, email)