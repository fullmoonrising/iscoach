package com.telegram.folobot.mappers

import com.telegram.folobot.domain.FoloPidorEntity
import com.telegram.folobot.dto.FoloPidorDto
import org.springframework.stereotype.Component

@Component
class FoloPidorMapper(private val foloUserMapper: FoloUserMapper) {

    fun mapToFoloPidorDto(entity: FoloPidorEntity): FoloPidorDto {
        return FoloPidorDto(
            entity.id,
            foloUserMapper.mapToFoloUserDto(entity.foloUserEntity),
            entity.score,
            entity.lastWinDate,
            entity.lastActiveDate
        )
    }

    fun mapToFoloPidorEntity(dto: FoloPidorDto): FoloPidorEntity {
        return FoloPidorEntity(
            dto.id,
            foloUserMapper.mapToFoloUserEntity(dto.foloUser),
            dto.score,
            dto.lastWinDate,
            dto.lastActiveDate
        )
    }
}