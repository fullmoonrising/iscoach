package com.telegram.folobot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class FoloUser {
    @Id
    private Long userid;

    private String name;
    private String tag;

    public FoloUser() {
    }

    public FoloUser(Long userid) { this(userid, null, null); }

    public FoloUser(Long userid, String tag) { this(userid, null, tag); }

    public FoloUser(Long userid, String name, String tag) {
        this.userid = userid;
        this.name = name;
        this.tag = tag;
    }

    public String getName() { return tag != null && !tag.isEmpty() ? tag : name != null ? name : ""; }

    public String getRealName() { return name != null ? name : ""; }

    public String getTag() { return tag != null ? tag : ""; }
}

