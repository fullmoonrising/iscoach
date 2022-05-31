package com.telegram.folobot.mappers;

import com.telegram.folobot.domain.FoloVarEntity;
import com.telegram.folobot.dto.FoloVarDto;
import org.springframework.stereotype.Component;

@Component
public class FoloVarMapper {
    public FoloVarDto mapToFoloVarDto(FoloVarEntity entity){
        return new FoloVarDto()
                .setId(entity.getId())
                .setValue(entity.getValue());
    }

    public FoloVarEntity mapToFoloVarEntity(FoloVarDto dto){
        return new FoloVarEntity()
                .setId(dto.getId())
                .setValue(dto.getValue());
    }
}
