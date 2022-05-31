package com.telegram.folobot.dto;

import com.telegram.folobot.domain.FoloVarId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class FoloVarDto implements Serializable {
    private FoloVarId id;
    private String value;
}
