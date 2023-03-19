package ru.iscoach.service.model.dto

import ru.iscoach.persistence.entity.NotificationEntity

data class NotificationDto(
    val userId: Long,
    val email: String
)

fun NotificationDto.toEntity() = NotificationEntity(userId, email)