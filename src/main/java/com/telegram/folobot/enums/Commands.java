package com.telegram.folobot.enums;

public enum Commands {
    SILENTSTREAM("/silentstream"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop");

    public final String label;

    Commands(String label) {
        this.label = label;
    }

    public static Commands valueOfLabel(String label) {
        for (Commands e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
