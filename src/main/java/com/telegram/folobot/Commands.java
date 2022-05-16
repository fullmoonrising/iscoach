package com.telegram.folobot;

public enum Commands {
    SILENTSTREAM("/silentstream"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop");

    public final String label;

    private Commands(String label) {
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
