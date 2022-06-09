package com.telegram.folobot.mappers

import com.telegram.folobot.domain.FoloUserEntity
import com.telegram.folobot.dto.FoloUserDto
import org.springframework.stereotype.Component

@Component
class FoloUserMapper {
    fun mapToFoloUserDto(entity: FoloUserEntity): FoloUserDto {
        return FoloUserDto(entity.userId, entity.mainId, entity.name , entity.tag)
    }

    fun mapToFoloUserEntity(dto: FoloUserDto): FoloUserEntity {
        return FoloUserEntity(dto.userId, dto.mainId, dto.name, dto.tag)
    }
}