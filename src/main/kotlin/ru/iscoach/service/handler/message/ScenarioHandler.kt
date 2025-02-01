package ru.iscoach.service.handler.message

import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.addActionReceived
import ru.iscoach.extrensions.chat
import ru.iscoach.extrensions.from
import ru.iscoach.extrensions.isNotForward
import ru.iscoach.service.scenario.ScenarioService
import ru.iscoach.service.model.type.Action
import ru.iscoach.service.model.type.BotCommand

@Component
@Priority(4)
class ScenarioHandler(
    private val scenarioService: ScenarioService,
) : AbstractMessageHandler() {
    fun Message?.isScenarioCommand() =
        this?.chat?.isUserChat == true &&
                this.isNotForward() &&
                BotCommand.isScenario(text)

    override fun canHandle(update: Update): Boolean =
        (super.canHandle(update) && (update.message.isScenarioCommand() || scenarioService.isActiveByUserId(update.from.id)))
            .also { if (it) logger.addActionReceived(Action.SCENARIO, update.chat) }

    override fun handle(update: Update) {
        scenarioService.runScenario(update)
    }
}