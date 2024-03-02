package ru.iscoach.service.model.type

enum class BotCommand(val command: String) {
    START("/start"),
    SESSION("/session"),
    MEDITATION("/meditation");
    companion object {
        private val map = entries.associateBy(BotCommand::command)
        fun fromCommand(command: String?) = map[command]
    }
}