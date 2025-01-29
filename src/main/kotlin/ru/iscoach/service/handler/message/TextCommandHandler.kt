package ru.iscoach.service.handler.message

import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.addActionReceived
import ru.iscoach.extrensions.addCommandReceived
import ru.iscoach.extrensions.chat
import ru.iscoach.extrensions.chatId
import ru.iscoach.extrensions.from
import ru.iscoach.extrensions.getMsg
import ru.iscoach.extrensions.isNotForward
import ru.iscoach.service.CommandService
import ru.iscoach.service.model.type.Action
import ru.iscoach.service.model.type.BotCommand
import ru.iscoach.service.scenario.ScenarioService

@Component
@Priority(2)
class TextCommandHandler(
    private val commandService: CommandService,
    private val scenarioService: ScenarioService,
) : AbstractMessageHandler() {
    fun Message?.isTextCommand() =
        this?.chat?.isUserChat == true &&
                this.isNotForward() &&
                BotCommand.isTextCommand(text)

    override fun canHandle(update: Update): Boolean = (super.canHandle(update) && update.message.isTextCommand())
        .also { if (it) logger.addActionReceived(Action.TEXTCOMMAND, update.chat) }

    override fun handle(update: Update) {
        when (
            BotCommand.fromTextCommand((update.getMsg().text))
                .also { logger.addCommandReceived(it, update.from, update.chat) }
        ) {
            BotCommand.SESSION -> commandService.sendSessionMenu(update)
            BotCommand.MEDITATION -> commandService.sendMeditationMenu(update)
            BotCommand.CANCEL -> scenarioService.interruptScenario(update.from.id)
            else -> {}
        }
    }
}