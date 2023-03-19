package ru.iscoach.persistence.repos

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.iscoach.persistence.entity.NotificationEntity

@Repository
interface NotificationRepo : CrudRepository<NotificationEntity, Long> {
    fun findByUserId(userId: Long): NotificationEntity?
}