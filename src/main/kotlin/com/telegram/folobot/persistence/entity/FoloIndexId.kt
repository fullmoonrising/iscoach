package com.telegram.folobot.persistence.entity

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.Embeddable

@Embeddable
data class FoloIndexId(
    private val chatId: Long,
    private val date: LocalDate = LocalDate.now()
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoloIndexId

        if (chatId != other.chatId) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}