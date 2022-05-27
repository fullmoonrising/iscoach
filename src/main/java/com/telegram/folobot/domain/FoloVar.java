package com.telegram.folobot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class FoloVar {
    @EmbeddedId
    private FoloVarId id;
    private String value;

    public FoloVar() {};

    public FoloVar(FoloVarId id) { this(id, ""); }

    public FoloVar(FoloVarId id, String value) {
        this.id = id;
        this.value = value;
    }
}

