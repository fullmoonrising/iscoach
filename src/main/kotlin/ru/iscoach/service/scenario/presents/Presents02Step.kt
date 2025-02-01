package ru.iscoach.service.scenario.presents

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.service.KeyboardBuilder
import ru.iscoach.service.MessageService
import ru.iscoach.service.scenario.ScenarioStep

@Component
class Presents02Step(
    private val messageService: MessageService,
    private val keyboardBuilder: KeyboardBuilder,
    override var nextStep: Presents03Step
): ScenarioStep {

    override fun validateRequirements(update: Update): Boolean = true

    override fun sendRequirementsNotMetMessage(userId: Long) {

    }

    override fun sendRequest(userId: Long) {
        messageService.sendMessage(
            text =
                """
                    Я приготовила много подарков для Вас в этом боте - помощнике.

                    Чтобы забрать ПОДАРОК, напишите свою электронную почту и скорее забирайте подарки🎁
                """.trimIndent(),
            chatId = userId,
            replyMarkup = keyboardBuilder.removeKeyboard(),
            disableWebPagePreview = true
        )
    }
}