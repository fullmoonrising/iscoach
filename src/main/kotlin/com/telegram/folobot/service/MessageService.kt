package com.telegram.folobot.service

import com.telegram.folobot.FoloBot
import com.telegram.folobot.Utils.printExeptionMessage
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.ActionType
import org.telegram.telegrambots.meta.api.methods.ForwardMessage
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.*

@Component
class MessageService { //TODO kotlinise
    lateinit var foloBot: FoloBot

    /**
     * Собрать объект [SendMessage]
     *
     * @param text   Текст сообщения
     * @param update [Update]
     * @return [SendMessage]
     */
    fun buildMessage(text: String, update: Update): SendMessage {
        return SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWN)
            .chatId(update.message.chatId.toString())
            .text(text)
            .build()
    }

    /**
     * Собрать объект [SendMessage]
     *
     * @param text   Текст сообщения
     * @param update [Update]
     * @param reply  В ответ на сообщение
     * @return [SendMessage]
     */
    fun buildMessage(text: String, update: Update, reply: Boolean): SendMessage {
        val sendMessage: SendMessage = buildMessage(text, update)
        if (reply) sendMessage.replyToMessageId = update.message.messageId
        return sendMessage
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param chatid ID чата(пользователя)
     */
    fun sendMessage(text: String, chatid: Long) {
        try {
            foloBot.execute(
                SendMessage
                    .builder()
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(chatid.toString())
                    .text(text)
                    .build()
            )
        } catch (e: TelegramApiException) {
            printExeptionMessage(e)
        }
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param update [Update]
     * @return [Message]
     */
    fun sendMessage(text: String, update: Update): Message? {
        try {
            return foloBot.execute(buildMessage(text, update))
        } catch (e: TelegramApiException) {
            printExeptionMessage(e)
        }
        return null
    }

    /**
     * Отправить сообщение буз реплая
     *
     * @param text   текст сообщения
     * @param update [Update]
     * @param reply  да/нет
     * @return [Message]
     */
    fun sendMessage(text: String, update: Update, reply: Boolean): Message? {
        if (!reply) {
            return sendMessage(text, update)
        } else {
            try {
                return foloBot.execute(buildMessage(text, update, reply))
            } catch (e: TelegramApiException) {
                printExeptionMessage(e)
            }
        }
        return null
    }

    private fun buildSticker(stickerFile: InputFile?, update: Update): SendSticker? {
        return if (stickerFile != null) {
            SendSticker
                .builder()
                .chatId(update.message.chatId.toString())
                .sticker(stickerFile)
                .replyToMessageId(update.message.messageId)
                .build()
        } else null
    }

    /**
     * Отправить стикер
     *
     * @param stickerFile [InputFile]
     * @param update      [Update]
     */
    fun sendSticker(stickerFile: InputFile?, update: Update) {
        if (stickerFile != null) {
            try {
                foloBot.execute(buildSticker(stickerFile, update))
            } catch (e: TelegramApiException) {
                printExeptionMessage(e)
            }
        }
    }

    /**
     * Пересылка сообщений
     *
     * @param chatid Чат куда будет переслано сообщение
     * @param update [Update]
     */
    fun forwardMessage(chatid: Long, update: Update) {
        try {
            foloBot.execute(
                ForwardMessage
                    .builder()
                    .chatId((chatid).toString())
                    .messageId(update.message.messageId)
                    .fromChatId(update.message.chatId.toString())
                    .build()
            )
        } catch (e: TelegramApiException) {
            printExeptionMessage(e)
        }
    }

    fun forwardMessage(chatid: Long, message: Message?) {
        if (message != null) {
            try {
                foloBot.execute(
                    ForwardMessage
                        .builder()
                        .chatId(chatid.toString())
                        .messageId(message.messageId)
                        .fromChatId(message.chatId.toString())
                        .build()
                )
            } catch (e: TelegramApiException) {
                printExeptionMessage(e)
            }
        }
    }

    /**
     * Отправить статус "печатает"
     *
     * @param update [Update]
     */
    fun sendChatTyping(update: Update) {
        try {
            foloBot.execute(
                SendChatAction
                    .builder()
                    .chatId(update.message.chatId.toString())
                    .action(ActionType.TYPING.toString())
                    .build()
            )
        } catch (e: TelegramApiException) {
            printExeptionMessage(e)
        }
    }

    /**
     * Получить случайный стикер (из гиф пака)
     *
     * @return [InputFile]
     */
    val randomSticker: InputFile
        get() {
            val stickers = arrayOf(
                "CAACAgIAAxkBAAICCGKCCI-Ff-uqMZ-y4e0YmQEAAXp_RQAClxQAAnmaGEtOsbVbM13tniQE",
                "CAACAgIAAxkBAAPpYn7LsjgOH0OSJFBGx6WoIIKr_vcAAmQZAAJgRSBL_cLL_Nrl4OskBA",
                "CAACAgIAAxkBAAICCWKCCLoO6Itf6HSKKGedTPzbyeioAAJQFAACey0pSznSfTz0daK-JAQ",
                "CAACAgIAAxkBAAICCmKCCN_lePGRwqFYK4cPGBD4k_lpAAJcGQACmGshS9K8iR0VSuDVJAQ"
            )
            return InputFile(stickers[SplittableRandom().nextInt(stickers.size)])
        }
}