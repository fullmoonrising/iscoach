package ru.iscoach.service.scenario.presents

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.service.KeyboardBuilder
import ru.iscoach.service.MessageService
import ru.iscoach.service.scenario.ScenarioStep

@Component
class Presents01Step(
    private val messageService: MessageService,
    private val keyboardBuilder: KeyboardBuilder,
    override var nextStep: Presents02Step
): ScenarioStep {
    override fun validateRequirements(update: Update): Boolean = true

    override fun sendRequirementsNotMetMessage(userId: Long) {

    }

    override fun sendRequest(userId: Long) {
        messageService.sendPhoto(
            photoPath = "/static/images/scenario/presents/presents01.jpg",
            chatId = userId,
            text =
            """
                Рада приветствовать Вас!
                Меня зовут Ирина Аравина, я уже ~ 10 лет в самопознании и психологии. Мой опыт насчитывает 1700+ часов терапии  людей и их жизненных ситуаций. 

                Сейчас для Вас есть кое-что интересное.

                Продолжаем?
            """.trimIndent(),
            replyMarkup = keyboardBuilder.buildPresentsSimpleMenu()
        )
    }
}