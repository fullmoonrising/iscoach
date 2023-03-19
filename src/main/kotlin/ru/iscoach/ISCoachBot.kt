package ru.iscoach

import ru.iscoach.config.BotCredentialsConfig
import ru.iscoach.event.UpdateReceivedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
final class ISCoachBot(
    private val botCredentials: BotCredentialsConfig,
    private val applicationEventPublisher: ApplicationEventPublisher
) : TelegramLongPollingBot(botCredentials.botToken) {
    override fun getBotUsername() = botCredentials.botUsername
    override fun onUpdateReceived(update: Update) = applicationEventPublisher.publishEvent(UpdateReceivedEvent(update))
}