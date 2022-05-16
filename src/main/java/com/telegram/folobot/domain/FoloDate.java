package com.telegram.folobot.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Date;

@Entity // This tells Hibernate to make a table out of this class
@IdClass(FoloDateId.class)
public class FoloDate {
    @Id
    private long chatid;
    @Id
    private String type;
    private Date date;

    public FoloDate() {
    }

    public FoloDate(long chatid, String type) {
        this(chatid, type, new Date());
    }

    public FoloDate(long chatid, String type, Date date) {
        this.chatid = chatid;
        this.type = type;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

