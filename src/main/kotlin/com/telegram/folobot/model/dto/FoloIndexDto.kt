package com.telegram.folobot.model.dto

import com.telegram.folobot.persistence.entity.FoloIndexEntity
import com.telegram.folobot.persistence.entity.FoloIndexId
import com.telegram.folobot.persistence.entity.FoloPidorEntity
import com.telegram.folobot.persistence.entity.FoloPidorId
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Objects
import kotlin.math.absoluteValue

data class FoloIndexDto(
    val id: FoloIndexId,
    var points: Int = 0,
    var index: Double? = null
) {
    constructor(chatId: Long, date:LocalDate) : this(FoloIndexId(chatId, date))
    fun addPoints(points: Int): FoloIndexDto {
        this.points += points
        return this
    }

    fun setIndex(index: Double): FoloIndexDto {
        this.index = index
        return this
    }
}

fun FoloIndexDto.toEntity(): FoloIndexEntity = FoloIndexEntity(
    id = this.id,
    points = this.points,
    index = this.index
)