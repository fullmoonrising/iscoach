package com.telegram.folobot.constants

enum class BotCommandsEnum(val command: String) {
    START("/start"),
    SILENTSTREAM("/silentstream"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop"),
    FOLOSLACKERS("/foloslackers"),
    FOLOUNDERDOGS("/folounderdogs"),
    FOLOPIDORALPHA("/folopidoralpha");

    companion object {
        private val map = BotCommandsEnum.values().associateBy(BotCommandsEnum::command)
        fun fromCommand(command: String) = map[command]
    }
}