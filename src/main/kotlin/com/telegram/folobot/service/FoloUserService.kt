package com.telegram.folobot.service

import com.telegram.folobot.persistence.dto.FoloUserDto
import com.telegram.folobot.persistence.entity.FoloUserEntity
import com.telegram.folobot.persistence.mappers.FoloUserMapper
import com.telegram.folobot.persistence.repos.FoloUserRepo
import org.springframework.data.util.Streamable
import org.springframework.stereotype.Component

@Component
class FoloUserService(private val foloUserRepo: FoloUserRepo,
                      private val foloUserMapper: FoloUserMapper) {
    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloUserDto> {
        return Streamable.of(foloUserRepo.findAll()).toList()
            .map(foloUserMapper::mapToFoloUserDto)
    }

    /**
     * Чтение по Id
     * @param userId [Long] Id
     * @return [FoloUserDto]
     */
    fun findById(userId: Long): FoloUserDto {
        return foloUserMapper.mapToFoloUserDto(
            foloUserRepo.findById(userId)
                .orElse(FoloUserEntity(userId))
        )
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
        foloUserRepo.save(foloUserMapper.mapToFoloUserEntity(dto))
    }

    /**
     * Удаление
     * @param dto [FoloUserDto]
     */
    fun delete(dto: FoloUserDto) {
        foloUserRepo.delete(foloUserMapper.mapToFoloUserEntity(dto))
    }
}