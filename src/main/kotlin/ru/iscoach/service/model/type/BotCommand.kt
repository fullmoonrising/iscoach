package ru.iscoach.service.model.type

enum class BotCommand(
    val command: String,
    val textCommand: String? = null,
    val isScenario: Boolean = false
) {
    START("/start"),
    WEB_APP("/web_app", "Сайт"),
    SESSION("/session", "Сессия"),
    MEDITATION("/meditation", "Медитация"),
    FREE_STUFF("/free_stuff", "Подарки", true),
    OK("/ok", "Продолжить", true),
    CANCEL("/cancel","Отмена");

    companion object {
        private val commands = entries.associateBy { it.command }
        private val textCommands = entries.associateBy { it.textCommand }

        fun fromCommand(command: String?): BotCommand? = commands[command]

        fun fromTextCommand(text: String?): BotCommand? = textCommands[text]

        fun isTextCommand(text: String?): Boolean = fromTextCommand(text)?.isScenario == false

        fun isScenario(text: String?): Boolean = fromTextCommand(text)?.isScenario == true
    }
}

