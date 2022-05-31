package com.telegram.folobot.mappers;

import com.telegram.folobot.domain.FoloPidorEntity;
import com.telegram.folobot.dto.FoloPidorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FoloPidorMapper {
    private final FoloUserMapper foloUserMapper;
    public FoloPidorDto mapToFoloPidorDto(FoloPidorEntity entity){
        return new FoloPidorDto()
                .setId(entity.getId())
                .setFoloUser(foloUserMapper.mapToFoloUserDto(entity.getFoloUserEntity()))
                .setScore(entity.getScore());
    }

    public FoloPidorEntity mapToFoloPidorEntity(FoloPidorDto dto){
        return new FoloPidorEntity()
                .setId(dto.getId())
                .setFoloUserEntity(foloUserMapper.mapToFoloUserEntity(dto.getFoloUser()))
                .setScore(dto.getScore());
    }
}
