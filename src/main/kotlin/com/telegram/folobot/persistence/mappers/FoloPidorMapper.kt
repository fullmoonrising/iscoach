package com.telegram.folobot.persistence.mappers

import com.telegram.folobot.persistence.dto.FoloPidorDto
import com.telegram.folobot.persistence.entity.FoloPidorEntity
import org.springframework.stereotype.Component

@Component
class FoloPidorMapper(private val foloUserMapper: FoloUserMapper) {

    fun mapToFoloPidorDto(entity: FoloPidorEntity): FoloPidorDto {
        return FoloPidorDto(
            entity.id,
            foloUserMapper.mapToFoloUserDto(entity.foloUserEntity),
            entity.score,
            entity.lastWinDate,
            entity.lastActiveDate,
            entity.messagesPerDay
        )
    }

    fun mapToFoloPidorEntity(dto: FoloPidorDto): FoloPidorEntity {
        return FoloPidorEntity(
            dto.id,
            foloUserMapper.mapToFoloUserEntity(dto.foloUser),
            dto.score,
            dto.lastWinDate,
            dto.lastActiveDate,
            dto.messagesPerDay
        )
    }
}