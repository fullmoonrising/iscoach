package ru.iscoach.service.scenario

import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.addCommandReceived
import ru.iscoach.extrensions.chat
import ru.iscoach.extrensions.from
import ru.iscoach.extrensions.getMsg
import ru.iscoach.service.KeyboardBuilder
import ru.iscoach.service.MessageService
import ru.iscoach.service.model.type.BotCommand
import ru.iscoach.service.scenario.presents.Presents01Step

@Service
class ScenarioService(
    private val messageService: MessageService,
    private val presents01Step: Presents01Step,
    private val keyboardBuilder: KeyboardBuilder,
): KLogging() {
    private val activeScenario = mutableMapOf<Long, Scenario>()

    fun isActiveByUserId(userId: Long): Boolean = activeScenario.containsKey(userId)

    fun runScenario(update: Update) {
        activeScenario[update.from.id]
            ?.doNextRequestAndRemoveIfScenarioCompleted(update)
            ?: when (
                BotCommand.fromTextCommand((update.getMsg().text))
                    .also { logger.addCommandReceived(it, update.from, update.chat) }
            ) {
                BotCommand.FREE_STUFF -> {
                    Scenario(presents01Step)
                        .let { scenario ->
                            activeScenario[update.from.id] = scenario
                            scenario.doNextRequestAndRemoveIfScenarioCompleted(update)
                        }
                }

                else -> {}
            }
    }

    fun interruptScenario(userId: Long): Scenario? =
        activeScenario.remove(userId)
            .also {
                messageService.sendMessage(
                    text = "Отменено",
                    chatId = userId,
                    replyMarkup = keyboardBuilder.buildMainMenu()
                )
            }

    private fun Scenario.doNextRequestAndRemoveIfScenarioCompleted(update: Update) {
        this.doNextRequest(update)
            .takeIf { it }
            ?.let { activeScenario.remove(update.from.id) }
    }
}