package com.telegram.folobot.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
public class FoloVarId implements Serializable {
    private long chatId;
    private String type;

    public FoloVarId() {};

    public FoloVarId(Long chatId, String type) {
        this.chatId = chatId;
        this.type = type;
    }
}
