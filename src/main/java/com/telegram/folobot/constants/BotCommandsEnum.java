package com.telegram.folobot.constants;

public enum BotCommandsEnum {
    SILENTSTREAM("/silentstream"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop");

    public final String label;

    BotCommandsEnum(String label) {
        this.label = label;
    }

    public static BotCommandsEnum valueOfLabel(String label) {
        for (BotCommandsEnum command : values()) {
            if (command.label.equals(label)) {
                return command;
            }
        }
        return null;
    }
}
