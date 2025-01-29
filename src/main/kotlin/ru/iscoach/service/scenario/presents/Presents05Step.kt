package ru.iscoach.service.scenario.presents

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.service.KeyboardBuilder
import ru.iscoach.service.MessageService
import ru.iscoach.service.scenario.ScenarioStep

@Component
class Presents05Step(
    private val messageService: MessageService,
    private val keyboardBuilder: KeyboardBuilder,
    override var nextStep: Presents06Step,
) : ScenarioStep {

    override fun validateRequirements(update: Update): Boolean = true

    override fun sendRequirementsNotMetMessage(userId: Long) {

    }

    override fun sendRequest(userId: Long) {
        messageService.sendMessage(
            text =
                """
                    Так ли хорошо Вы знаете себя? 
                    *Что Вы любите?* 
                    Когда тепло? Или когда холодно? Дождливую погоду или солнечную? 
                    
                    Забирайте Ваши подарки по ссылке (можно скачать):
                    
                    [🔖ПРАКТИКА ПО ВЫЯВЛЕНИЮ ВАШИХ ЦЕННОСТЕЙ](https://t.me/monakocoach/686)
                    
                    [🔖ГАЙД ВАШ УРОВЕНЬ ЭНЕРГИИ](https://t.me/monakocoach/711)
                """.trimIndent(),
            chatId = userId,
            replyMarkup = keyboardBuilder.buildPresentsSimpleMenu(),
            disableWebPagePreview = true,
        )
    }
}