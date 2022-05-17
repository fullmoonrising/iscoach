package com.telegram.folobot.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity // This tells Hibernate to make a table out of this class
@IdClass(FoloPidorId.class)
public class FoloPidor {
    @Id
    protected Long chatid;
    @Id
    protected Long userid;
    protected Integer score;
    protected String tag;

    public FoloPidor() {
    }

    public FoloPidor(long chatid, long userid) {
        this(chatid, userid, 0);
    }

    public FoloPidor(Long chatid, Long userid, Integer score) {
        this(chatid, userid, score, "");
    }

    public FoloPidor(Long chatid, Long userid, String tag) {
        this(chatid, userid, 0, tag);
    }

    public FoloPidor(Long chatid, Long userid, Integer score, String tag) {
        this.chatid = chatid;
        this.userid = userid;
        this.score = score;
        this.tag = tag;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isEmpty() {
        return this.chatid == null || this.userid == null;
    }
}

