package com.telegram.folobot.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class FoloUser {
    @Id
    private long userid;
    private String name;

    public FoloUser() {}

    public FoloUser(long userid, String name) {
        this.userid = userid;
        this.name = name;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

