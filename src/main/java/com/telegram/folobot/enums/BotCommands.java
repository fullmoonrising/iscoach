package com.telegram.folobot.enums;

public enum BotCommands {
    SILENTSTREAM("/silentstream"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop");

    public final String label;

    BotCommands(String label) {
        this.label = label;
    }

    public static BotCommands valueOfLabel(String label) {
        for (BotCommands command : values()) {
            if (command.label.equals(label)) {
                return command;
            }
        }
        return null;
    }
}
