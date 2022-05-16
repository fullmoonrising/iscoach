package com.telegram.folobot.domain;

import java.io.Serializable;
import java.util.Objects;

public class FoloPidorId implements Serializable {
    private long chatid;
    private long userid;

    // default constructor
    public FoloPidorId() {};

    public FoloPidorId(long chatid, long userid) {
        this.chatid = chatid;
        this.userid = userid;
    }

    public long getChatid() {
        return chatid;
    }

    public void setChatid(long chatid) {
        this.chatid = chatid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoloPidorId)) return false;
        FoloPidorId that = (FoloPidorId) o;
        return chatid == that.chatid && userid == that.userid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatid, userid);
    }
}
