package com.telegram.folobot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(FoloVarId.class)
@Getter
@Setter
public class FoloVar {
    @Id
    private long chatid;
    @Id
    private String type;
    private String value;

    public FoloVar() {
    }

    public FoloVar(long chatid, String type) {
        this(chatid, type, "");
    }

    public FoloVar(long chatid, String type, String value) {
        this.chatid = chatid;
        this.type = type;
        this.value = value;
    }
}

