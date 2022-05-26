package com.telegram.folobot.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
public class FoloPidorId implements Serializable {
    private long chatid;
    private long userid;

    public FoloPidorId() {}

    public FoloPidorId(long chatid, long userid) {
        this.chatid = chatid;
        this.userid = userid;
    }
}
