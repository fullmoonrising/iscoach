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

    public FoloUser() {
    }

    public FoloUser(Long userid, String name) {
        this.userid = userid;
        this.name = name;
    }

    public boolean isEmpty() {
        return this.userid == null;
    }
}

