package com.telegram.folobot.constants

enum class BotCommandsEnum(val label: String) {
    SILENTSTREAM("/silentstream"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop");

    companion object {
        fun valueOfLabel(label: String): BotCommandsEnum? {
            for (command in values()) {
                if (command.label == label) {
                    return command
                }
            }
            return null
        }
    }
}