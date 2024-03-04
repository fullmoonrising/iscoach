package ru.iscoach.persistence.repos

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.iscoach.persistence.entity.DonateSessionEntity
import java.util.UUID

@Repository
interface DonateSessionRepo : CrudRepository<DonateSessionEntity, UUID> {
    fun findTopByOrderByDateTimeDesc(): DonateSessionEntity?
}