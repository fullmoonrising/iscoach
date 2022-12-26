package com.telegram.folobot.service

import com.telegram.folobot.IdUtils.Companion.getChatIdentity
import com.telegram.folobot.IdUtils.Companion.isAboutFo
import com.telegram.folobot.IdUtils.Companion.isFo
import com.telegram.folobot.model.dto.FoloIndexDto
import com.telegram.folobot.model.dto.toEntity
import com.telegram.folobot.persistence.entity.FoloIndexId
import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloIndexRepo
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate

@Service
class FoloIndexService(
    private val foloIndexRepo: FoloIndexRepo
) : KLogging() {
    fun getById(chatId: Long, date: LocalDate): FoloIndexDto {
        return foloIndexRepo.findIndexById(FoloIndexId(chatId, date))?.toDto()
            ?: FoloIndexDto(chatId, date)
    }

    fun addActivityPoints(update: Update) {
        if (update.hasMessage()) {
            val points =
                if (isFo(update.message.from)) 3
                else if (isAboutFo(update)) 2
                else 1
            foloIndexRepo.save(getById(update.message.chatId, LocalDate.now()).addPoints(points).toEntity())
            logger.info { "Added $points points to chat ${getChatIdentity(update.message.chatId)}" }
        }
    }

    fun getAveragePoints(chatId: Long, date: LocalDate): Double? {
        return foloIndexRepo.getAveragePointsByIdChatId(chatId, date.minusYears(1), date)
    }

    fun calcAndSaveIndex(chatId: Long, date: LocalDate): Double {
        val foloIndex = getById(chatId, date)
        foloIndex.index = getAveragePoints(chatId, date)?.let { foloIndex.points / it * 100 } ?: 0.0
        foloIndexRepo.save(foloIndex.toEntity())
        return foloIndex.index!!
    }
}