package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendVoice
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot
import ru.iscoach.extrensions.chatId

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
    ) = SendMessage.builder()
        .parseMode(parseMode)
        .chatId(update.chatId)
        .text(text)
        .also { if (reply) it.replyToMessageId(update.message.messageId) }
        .also { sendMessage -> replyMarkup?.let<ReplyKeyboard, Unit> { sendMessage.replyMarkup(it) } }
        .build()

    fun sendMessage(
        text: String,
        update: Update,
        replyMarkup: ReplyKeyboard? = null,
        reply: Boolean = false,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? =
        try {
            bot.execute(buildMessage(text, update, replyMarkup, reply, parseMode))
        } catch (e: TelegramApiException) {
            logger.error(e) { "Message text was: $text" }
            null
        }

    fun deleteMessage(chatId: Long, messageId: Int): Boolean {
        return try {
            return bot.execute(DeleteMessage(chatId.toString(), messageId))
        } catch (e: TelegramApiException) {
            logger.error { e }
            false
        }
    }

    fun buildPhoto(
        photo: InputFile,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): SendPhoto = SendPhoto.builder()
        .parseMode(parseMode)
        .chatId(chatId.toString())
        .photo(photo)
        .also { sendPhoto -> text?.let { sendPhoto.caption(text) } }
        .also { sendPhoto -> replyMarkup?.let { sendPhoto.replyMarkup(replyMarkup) } }
        .build()

    fun buildPhoto(
        photoPath: String,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): SendPhoto = buildPhoto(
        InputFile(
            this::class.java.getResourceAsStream(photoPath),
            photoPath.substringAfterLast("/")
        ),
        chatId,
        text,
        replyMarkup,
        parseMode
    )

    fun sendPhoto(
        photoPath: String,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? {
        return try {
            bot.execute(buildPhoto(photoPath, chatId, text, replyMarkup, parseMode))
        } catch (e: Exception) {
            logger.error { e }
            null
        }
    }

    fun buildVoice(
        photo: InputFile,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): SendVoice = SendVoice.builder()
        .parseMode(parseMode)
        .chatId(chatId.toString())
        .voice(photo)
        .also { sendVoice -> text?.let { sendVoice.caption(text) } }
        .also { sendVoice -> replyMarkup?.let { sendVoice.replyMarkup(replyMarkup) } }
        .build()

    fun buildVoice(
        photoPath: String,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): SendVoice = buildVoice(
        InputFile(
            this::class.java.getResourceAsStream(photoPath),
            photoPath.substringAfterLast("/")
        ),
        chatId,
        text,
        replyMarkup,
        parseMode
    )
    fun sendVoice(
        voicePath: String,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ): Message? {
        return try {
            bot.execute(buildVoice(voicePath, chatId, text, replyMarkup, parseMode))
        } catch (e: Exception) {
            logger.error { e }
            null
        }
    }
}