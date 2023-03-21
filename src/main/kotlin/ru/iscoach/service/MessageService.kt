package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot

@Component
class MessageService(
    private val bot: ISCoachBot
) : KLogging() {

    private fun buildMessage(
        text: String,
        update: Update,
        replyMarkup: ReplyKeyboard? = null,
        reply: Boolean = false,
        parseMode: String = ParseMode.MARKDOWN
    ): SendMessage {
        val sendMessage = SendMessage
            .builder()
            .parseMode(parseMode)
            .chatId(update.message?.chatId ?: update.callbackQuery.message.chatId)
            .text(text)
        if (reply) sendMessage.replyToMessageId(update.message.messageId)
        replyMarkup?.let { sendMessage.replyMarkup(replyMarkup) }
        return sendMessage.build()
    }

    fun sendMessage(
        text: String,
        update: Update,
        replyMarkup: ReplyKeyboard? = null,
        reply: Boolean = false,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? {
        return try {
            return bot.execute(buildMessage(text, update, replyMarkup, reply, parseMode))
        } catch (ex: TelegramApiException) {
            logger.error { ex }
            null
        }
    }

    fun deleteMessage(chatId: Long, messageId: Int): Boolean {
        return try {
            return bot.execute(DeleteMessage(chatId.toString(), messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
            false
        }
    }
}