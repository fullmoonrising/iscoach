package com.telegram.folobot.domain;

import java.io.Serializable;
import java.util.Objects;

public class FoloDateId implements Serializable {
    private long chatid;
    private String type;

    // default constructor
    public FoloDateId() {};

    public FoloDateId(long chatid,String type) {
        this.chatid = chatid;
        this.type = type;
    }

    public long getChatid() {
        return chatid;
    }

    public void setChatid(long chatid) {
        this.chatid = chatid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoloDateId)) return false;
        FoloDateId that = (FoloDateId) o;
        return getChatid() == that.getChatid() && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChatid(), getType());
    }
}
