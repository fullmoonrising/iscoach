package ru.iscoach.service

import org.springframework.stereotype.Service
import ru.iscoach.extrensions.toOffsetAtStartOfDay
import ru.iscoach.persistence.entity.DonateSessionEntity
import ru.iscoach.persistence.repos.DonateSessionRepo
import java.time.LocalDate

@Service
class DonateSessionService(
    private val repo: DonateSessionRepo,
) {
    fun checkIfBookedToday(): Boolean =
        repo.findTopByOrderByDateTimeDesc()
            ?.dateTime?.let { it > LocalDate.now().toOffsetAtStartOfDay() }
            ?: false

    fun registerBooking() {
        repo.save(DonateSessionEntity())
    }
}