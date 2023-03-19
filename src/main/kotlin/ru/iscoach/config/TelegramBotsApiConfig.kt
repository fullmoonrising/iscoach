package ru.iscoach.config

import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import ru.iscoach.ISCoachBot

@Configuration
class TelegramBotsApiConfig(foloBot: ISCoachBot) {
    init { TelegramBotsApi(DefaultBotSession::class.java).registerBot(foloBot) }
}