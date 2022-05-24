package com.telegram.folobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@IdClass(FoloPidorId.class)
@Getter
@Setter
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

    @Transient
    public boolean isNew() {
        return isNew;
    }

    public static FoloPidor createNew(Long chatid, Long userid) {
        FoloPidor foloPidor = new FoloPidor(chatid, userid);
        foloPidor.setNew(true);
        return foloPidor;
    }


    public boolean isEmpty() {
        return this.chatid == null || this.userid == null;
    }

    public boolean hasScore() {
        return this.score > 0;
    }

}

