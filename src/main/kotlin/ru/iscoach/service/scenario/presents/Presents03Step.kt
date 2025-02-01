package ru.iscoach.service.scenario.presents

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.service.KeyboardBuilder
import ru.iscoach.service.MessageService
import ru.iscoach.service.scenario.ScenarioStep

@Component
class Presents03Step(
    private val messageService: MessageService,
    private val keyboardBuilder: KeyboardBuilder,
    override var nextStep: Presents04Step
): ScenarioStep {

    override fun validateRequirements(update: Update): Boolean = true

    override fun sendRequirementsNotMetMessage(userId: Long) {

    }

    override fun sendRequest(userId: Long) {
        messageService.sendMessage(
            text =
                """
                    🎙️Всем ли стоит идти в терапию? 
                    Аудио подкаст уже [ждет Вас здесь](https://t.me/monakocoach/856)
                    
                    [СЛУШАТЬ](https://t.me/monakocoach/856)
                    [СЛУШАТЬ](https://t.me/monakocoach/856)
                    [СЛУШАТЬ](https://t.me/monakocoach/856)
                """.trimIndent(),
            chatId = userId,
            replyMarkup = keyboardBuilder.buildPresentsSimpleMenu(),
            disableWebPagePreview = true
        )
    }
}