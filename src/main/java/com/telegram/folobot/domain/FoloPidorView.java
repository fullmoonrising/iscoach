package com.telegram.folobot.domain;

public class FoloPidorView extends FoloPidor { //TODO переделать на джойн

    private String name;

    public FoloPidorView() {};

    public FoloPidorView(FoloPidor foloPidor) {
        this.chatid = foloPidor.chatid;
        this.userid = foloPidor.userid;
        this.score = foloPidor.score;
        this.tag = foloPidor.tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        } else {
            this.name = "-";
        }
    }
}

