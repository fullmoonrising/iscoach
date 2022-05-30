package com.telegram.folobot.dto;

import com.telegram.folobot.domain.FoloPidorId;
import com.telegram.folobot.domain.FoloUserEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class FoloPidorDto implements Serializable {
    private final FoloPidorId id;
    private final FoloUserEntity foloUserEntity;
    private final Integer score;
}
