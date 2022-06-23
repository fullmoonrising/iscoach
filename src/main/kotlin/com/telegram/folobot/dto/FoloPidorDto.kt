package com.telegram.folobot.dto

import com.telegram.folobot.domain.FoloPidorId
import java.io.Serializable
import java.time.LocalDate
import java.util.*

class FoloPidorDto(
    val id: FoloPidorId,
    var foloUser: FoloUserDto = FoloUserDto(id.userId),
    var score: Int = 0,
    var lastWinDate: LocalDate = LocalDate.of(1900,1,1)
) : Serializable {
    constructor(chatId: Long, userId: Long) : this(FoloPidorId(chatId, userId))
    constructor(chatId: Long, userId: Long, score: Int) : this(FoloPidorId(chatId, userId), FoloUserDto(userId), score)

    /**
     * Получить основоного пользователя
     */
    fun getMainUserId(): Long { return foloUser.getMainUserId() }

    /**
     * Получить тег, если он пуст то имя
     */
    fun getTagName(): String { return foloUser.getTagName() }

    /**
     * Получить имя пользователя
     */
    fun getName(): String { return foloUser.name }

    /**
     * Получить тэг
     */
    fun getTag(): String { return foloUser.tag }

    /**
     * Проверка наличичия побед
     */
    fun hasScore(): Boolean { return score > 0 }

    /**
     * Проверка что на твинка
     */
    fun isTwink(): Boolean { return foloUser.userId != foloUser.getMainUserId() }

    /**
     * Проверка валидности топа
     */
    fun isValid(): Boolean { return hasScore() && !isTwink() }

    /**
     * Проверка наличия якоря
     */
    fun isAnchored(): Boolean { return  foloUser.anchor }

    /**
     * Обновить счет и вернуть себя
     * @return [FoloPidorDto]
     */
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
        return Objects.hash(getMainUserId())
    }
}