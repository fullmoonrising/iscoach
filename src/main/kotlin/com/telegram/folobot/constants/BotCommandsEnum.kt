package com.telegram.folobot.constants

enum class BotCommandsEnum(val command: String) {
    SILENTSTREAM("/silentstream"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop"),
    FOLOUNDERDOG("/folounderdog"),
    FOLOPIDORALPHA("/folopidoralpha");

    companion object {
        private val map = BotCommandsEnum.values().associateBy(BotCommandsEnum::command)
        fun fromCommand(command: String) = map[command]
    }
}