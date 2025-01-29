package ru.iscoach.service.scenario.presents

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.service.KeyboardBuilder
import ru.iscoach.service.MessageService
import ru.iscoach.service.scenario.ScenarioStep

@Component
class Presents06Step(
    private val messageService: MessageService,
    private val keyboardBuilder: KeyboardBuilder
): ScenarioStep {
    override val nextStep: ScenarioStep? = null

    override fun validateRequirements(update: Update): Boolean = true

    override fun sendRequirementsNotMetMessage(userId: Long) {

    }

    override fun sendRequest(userId: Long) {
        messageService.sendMessage(
            text =
                """
                    Прочтите отзывы тех, кто уже пришел ко мне на «Терапию Души»👇 
                    
                    [ОТЗЫВЫ](https://t.me/monakocoach/701) 
                    
                    Жду Вас на разборе Вашей жизненной ситуации! 
                    До новых встреч!
                """.trimIndent(),
            chatId = userId,
            replyMarkup = keyboardBuilder.buildMainMenu(),
            disableWebPagePreview = true
        )
    }
}