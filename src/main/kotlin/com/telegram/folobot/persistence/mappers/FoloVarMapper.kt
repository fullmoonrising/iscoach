package com.telegram.folobot.persistence.mappers

import com.telegram.folobot.persistence.dto.FoloVarDto
import com.telegram.folobot.persistence.entity.FoloVarEntity
import org.springframework.stereotype.Component

@Component
class FoloVarMapper {
    fun mapToFoloVarDto(entity: FoloVarEntity): FoloVarDto {
        return FoloVarDto(entity.id, entity.value)
    }

    fun mapToFoloVarEntity(dto: FoloVarDto): FoloVarEntity {
        return FoloVarEntity(dto.id, dto.value)
    }
}