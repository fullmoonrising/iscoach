package ru.iscoach.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.chatId
import ru.iscoach.service.model.ProductCategory

@Service
class CommandService(
    private val messageService: MessageService,
    private val keyboardBuilder: KeyboardBuilder
) {
    fun sendStartMessage(update: Update) {
        messageService.sendMessage(
            """
                _Добро пожаловать! 
                Меня зовут Ирэн Слюсар, life коуч. Я здесь, чтобы помочь Вам на пути саморазвития и достижения Ваших целей. 

                Независимо от того, что Вас вдохновляет или какие вызовы Вы принимаете, я готова быть вашим верным гидом и поддержкой. 

                Давайте вместе создадим план действий, обсудим Ваши мечты и построим стратегии для их достижения._ 

                *Готовы ли Вы начать этот увлекательный путь к лучшей версии себя?*
            """.trimIndent(),
            update)
    }

    fun sendSessionMenu(update: Update) {
        messageService.sendPhoto(
            "/static/images/session/session_title.jpg",
            update.chatId,
            null,
//            """
//                Текст давай
//            """.trimIndent(),
            keyboardBuilder.buildProductCategoryKeyboard(ProductCategory.SERVICE)
        )
    }

    fun sendMeditationMenu(update: Update) {
        messageService.sendPhoto(
            "/static/images/meditation/meditation_title.jpg",
            update.chatId,
            """
                *Медитация - это легкая практика, которая помогает успокоить ум, снять стресс и найти внутреннюю гармонию.*

                Она может быть полезна для: 
                ✅улучшения физического и психического здоровья
                ✅повышения концентрации
                ✅улучшения осознанности 
                ✅повышения общего благополучия 
                
                Медитация также может помочь 
                ✅улучшить качество сна 
                ✅снизить уровень тревоги и депрессии
                ✅укрепить иммунную систему 

                *Попробуйте авторские медитации от Ирэн Слюсар (лайф коуча) и почувствуйте пользу для себя уже в первый день!*
            """.trimIndent(),
            keyboardBuilder.buildProductCategoryKeyboard(ProductCategory.DIGITAL_GOODS)
        )
    }
}