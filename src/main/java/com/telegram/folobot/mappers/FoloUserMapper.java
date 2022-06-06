package com.telegram.folobot.mappers;

import com.telegram.folobot.domain.FoloUserEntity;
import com.telegram.folobot.dto.FoloUserDto;
import org.springframework.stereotype.Component;

@Component
public class FoloUserMapper {
    public FoloUserDto mapToFoloUserDto(FoloUserEntity entity){
        return new FoloUserDto()
                .setUserId(entity.getUserId())
                .setMainId(entity.getMainId())
                .setName(entity.getName())
                .setTag(entity.getTag());
    }

    public FoloUserEntity mapToFoloUserEntity(FoloUserDto dto){
        return new FoloUserEntity()
                .setUserId(dto.getUserId())
                .setMainId(dto.getMainId())
                .setName(dto.getName())
                .setTag(dto.getTag());
    }
}
