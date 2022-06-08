package com.telegram.folobot.dto

import com.telegram.folobot.domain.FoloPidorId
import java.io.Serializable
import java.util.*

class FoloPidorDto(
    val id: FoloPidorId,
    var foloUser: FoloUserDto = FoloUserDto(id.userId),
    var score: Int = 0
) : Serializable {


    constructor(chatId: Long, userId: Long) : this(FoloPidorId(chatId, userId))
    constructor(chatId: Long, userId: Long, score: Int) : this(FoloPidorId(chatId, userId), FoloUserDto(userId), score)

    /**
     * Получить основоного пользователя
     */
    fun getMainUserId(): Long { return foloUser.getMainId() }

    /**
     * Получить имя пользователя
     */
    fun getName(): String { return foloUser.getName() }

    /**
     * Получить тэг
     */
    fun getTag(): String { return foloUser.getTag() }

    /**
     * Проверка наличичия побед
     */
    fun hasScore(): Boolean { return score > 0 }

    fun updateScore(score: Int): FoloPidorDto {
        this.score = score
        return this
    }

    override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is FoloPidorDto) return false
                return getMainUserId() == other.getMainUserId()
        }

    override fun hashCode(): Int {
        return Objects.hash(getMainUserId());
    }
}