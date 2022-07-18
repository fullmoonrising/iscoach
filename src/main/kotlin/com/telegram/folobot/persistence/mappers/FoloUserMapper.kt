package com.telegram.folobot.persistence.mappers

import com.telegram.folobot.persistence.dto.FoloUserDto
import com.telegram.folobot.persistence.entity.FoloUserEntity
import org.springframework.stereotype.Component

@Component
class FoloUserMapper {
    fun mapToFoloUserDto(entity: FoloUserEntity): FoloUserDto {
        return FoloUserDto(entity.userId, entity.mainId, entity.anchor, entity.name , entity.tag)
    }

    fun mapToFoloUserEntity(dto: FoloUserDto): FoloUserEntity {
        return FoloUserEntity(dto.userId, dto.mainId, dto.anchor, dto.name, dto.tag)
    }
}