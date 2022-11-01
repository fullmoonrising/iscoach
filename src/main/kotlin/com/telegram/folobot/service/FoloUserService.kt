package com.telegram.folobot.service

import com.telegram.folobot.persistence.dto.FoloUserDto
import com.telegram.folobot.persistence.dto.toEntity
import com.telegram.folobot.persistence.entity.FoloUserEntity
import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloUserRepo
import org.springframework.stereotype.Component

@Component
class FoloUserService(private val foloUserRepo: FoloUserRepo) {
    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloUserDto> {
        return foloUserRepo.findAll().map { it.toDto() }
    }

    /**
     * Чтение по Id
     * @param userId [Long] Id
     * @return [FoloUserDto]
     */
    fun findById(userId: Long): FoloUserDto {
        return foloUserRepo.findUserByUserId(userId)?.toDto() ?: FoloUserEntity(userId).toDto()
    }

    /**
     * Проверка наличия
     * @param userId Id пользователя
     * @return да/нет
     */
    fun existsById(userId: Long): Boolean {
        return foloUserRepo.existsById(userId)
    }

    /**
     * Сохранение
     * @param dto [FoloUserDto]
     */
    fun save(dto: FoloUserDto) {
        foloUserRepo.save(dto.toEntity())
    }

    /**
     * Удаление
     * @param dto [FoloUserDto]
     */
    fun delete(dto: FoloUserDto) {
        foloUserRepo.delete(dto.toEntity())
    }
}