package com.telegram.folobot.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
public class FoloVarId implements Serializable {
    private long chatid;
    private String type;

    public FoloVarId() {}

    public FoloVarId(long chatid, String type) {
        this.chatid = chatid;
        this.type = type;
    }
}
