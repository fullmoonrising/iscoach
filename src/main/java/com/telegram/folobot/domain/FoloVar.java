package com.telegram.folobot.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity // This tells Hibernate to make a table out of this class
@IdClass(FoloVarId.class)
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

