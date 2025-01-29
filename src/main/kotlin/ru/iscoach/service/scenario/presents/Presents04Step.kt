package ru.iscoach.service.scenario.presents

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.service.KeyboardBuilder
import ru.iscoach.service.MessageService
import ru.iscoach.service.scenario.ScenarioStep

@Component
class Presents04Step(
    private val messageService: MessageService,
    private val keyboardBuilder: KeyboardBuilder,
    override var nextStep: Presents05Step,
): ScenarioStep {

    override fun validateRequirements(update: Update): Boolean = true

    override fun sendRequirementsNotMetMessage(userId: Long) {

    }

    override fun sendRequest(userId: Long) {
        messageService.sendMessage(
            text =
                """
                    Подпишитесь на [МОЙ ТГ КАНАЛ](https://t.me/monakocoach)
                    
                    *И забирайте следующие бонусы*
                """.trimIndent(),
            chatId = userId,
            replyMarkup = keyboardBuilder.buildPresentsSimpleMenu(),
            disableWebPagePreview = true
        )
    }
}