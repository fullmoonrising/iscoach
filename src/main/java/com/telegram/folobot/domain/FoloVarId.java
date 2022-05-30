package com.telegram.folobot.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class FoloVarId implements Serializable {
    private long chatId;
    private String type;

    public FoloVarId() {};

}
