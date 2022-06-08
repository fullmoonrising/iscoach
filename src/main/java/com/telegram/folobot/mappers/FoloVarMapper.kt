package com.telegram.folobot.mappers

import com.telegram.folobot.domain.FoloVarEntity
import com.telegram.folobot.dto.FoloVarDto
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