package ru.iscoach.service.handler.message

import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.config.BotCredentialsConfig
import ru.iscoach.extrensions.*
import ru.iscoach.service.CommandService
import ru.iscoach.service.model.type.Action
import ru.iscoach.service.model.type.BotCommand

@Component
@Priority(1)
class CommandHandler(
    private val commandService: CommandService,
    private val botCredentials: BotCredentialsConfig
) : AbstractMessageHandler() {
    fun Message?.isMyCommand() = this?.isCommand == true && this.isNotForward() && (this.chat?.isUserChat == true ||
            this.entities?.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text
                ?.contains(botCredentials.botUsername) == true)

    override fun canHandle(update: Update): Boolean = (super.canHandle(update) && update.message.isMyCommand())
        .also { if (it) logger.addActionReceived(Action.COMMAND, update.chat) }

    override fun handle(update: Update) {
        when (
            BotCommand.fromCommand(update.getMsg().getBotCommand())
                .also { logger.addCommandReceived(it, update.from, update.chat) }
        ) {
            BotCommand.START -> commandService.sendStartMessage(update)
            BotCommand.SESSION -> commandService.sendSessionMenu(update)
            BotCommand.MEDITATION -> commandService.sendMeditationMenu(update)
            else -> {}
        }
    }
}