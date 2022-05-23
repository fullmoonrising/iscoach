package com.telegram.folobot.domain;

import com.telegram.folobot.repos.FoloPidorRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Transient;
import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
@IdClass(FoloPidorId.class)
public class FoloPidor {
    @Id
    protected Long chatid;
    @Id
    protected Long userid;
    protected Integer score;
    protected String tag;

    @Transient
    private boolean isNew;

    public FoloPidor() {
    }

    public FoloPidor(Long chatid, Long userid) {
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

    public void setChatid(Long chatid) {
        this.chatid = chatid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Transient
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Transient
    public boolean isNew() {
        return isNew;
    }

    public boolean isEmpty() {
        return this.chatid == null || this.userid == null;
    }

    public boolean hasScore() {
        return this.score > 0;
    }

}

