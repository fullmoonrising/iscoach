package ru.iscoach.service.model

enum class BotCommand(val command: String) {
    START("/start"),
    SESSION("/session"),
    MEDITATION("/meditation");
    companion object {
        private val map = BotCommand.values().associateBy(BotCommand::command)
        fun fromCommand(command: String?) = map[command]
    }
}