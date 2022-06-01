package com.telegram.folobot.service;

import com.telegram.folobot.FoloBot;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.SplittableRandom;

import static com.telegram.folobot.Utils.printExeptionMessage;

@Component
@RequiredArgsConstructor
@Accessors(chain = true)
public class MessageService {

    @Setter
    private FoloBot foloBot;

    /**
     * Собрать объект {@link SendMessage}
     *
     * @param text   Текст сообщения
     * @param update {@link Update}
     * @return {@link SendMessage}
     */
    public SendMessage buildMessage(String text, Update update) {
        return SendMessage
                .builder()
                .parseMode(ParseMode.MARKDOWN)
                .chatId(Long.toString(update.getMessage().getChatId()))
                .text(text)
                .build();
    }

    /**
     * Собрать объект {@link SendMessage}
     *
     * @param text   Текст сообщения
     * @param update {@link Update}
     * @param reply  В ответ на сообщение
     * @return {@link SendMessage}
     */
    public SendMessage buildMessage(String text, Update update, boolean reply) {
        SendMessage sendMessage = buildMessage(text, update);
        if (reply) sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        return sendMessage;
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param chatid ID чата(пользователя)
     */
    public void sendMessage(String text, Long chatid) {
        try {
            foloBot.execute(SendMessage
                    .builder()
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(Long.toString(chatid))
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param update {@link Update}
     * @return {@link Message}
     */
    public Message sendMessage(String text, Update update) {
        try {
            return foloBot.execute(buildMessage(text, update));
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
        return null;
    }

    /**
     * Отправить сообщение буз реплая
     *
     * @param text   текст сообщения
     * @param update {@link Update}
     * @param reply  да/нет
     * @return {@link Message}
     */
    public Message sendMessage(String text, Update update, boolean reply) {
        if (!reply) {
            return sendMessage(text, update);
        } else {
            try {
                return foloBot.execute(buildMessage(text, update, reply));
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
        return null;
    }

    private SendSticker buildSticker(InputFile stickerFile, Update update) {
        if (stickerFile != null) {
            return SendSticker
                    .builder()
                    .chatId(Long.toString(update.getMessage().getChatId()))
                    .sticker(stickerFile)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build();
        }
        return null;
    }

    /**
     * Отправить стикер
     *
     * @param stickerFile {@link InputFile}
     * @param update      {@link Update}
     */
    public void sendSticker(InputFile stickerFile, Update update) {
        if (stickerFile != null) {
            try {
                foloBot.execute(buildSticker(stickerFile, update));
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
    }

    /**
     * Пересылка сообщений
     *
     * @param chatid Чат куда будет переслано сообщение
     * @param update {@link Update}
     */
    public void forwardMessage(Long chatid, Update update) {
        try {
            foloBot.execute(ForwardMessage
                    .builder()
                    .chatId(Long.toString(chatid))
                    .messageId(update.getMessage().getMessageId())
                    .fromChatId(Long.toString(update.getMessage().getChatId()))
                    .build());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
    }

    public void forwardMessage(Long chatid, Message message) {
        if (message != null) {
            try {
                foloBot.execute(ForwardMessage
                        .builder()
                        .chatId(Long.toString(chatid))
                        .messageId(message.getMessageId())
                        .fromChatId(Long.toString(message.getChatId()))
                        .build());
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
    }

    /**
     * Отправить статус "печатает"
     *
     * @param update {@link Update}
     */
    public void sendChatTyping(Update update) {
        try {
            foloBot.execute(SendChatAction
                    .builder()
                    .chatId(Long.toString(update.getMessage().getChatId()))
                    .action(String.valueOf(ActionType.TYPING))
                    .build());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
    }

    /**
     * Получить случайный стикер (из гиф пака)
     *
     * @return {@link InputFile}
     */
    public InputFile getRandomSticker() {
        String[] stickers = {
                "CAACAgIAAxkBAAICCGKCCI-Ff-uqMZ-y4e0YmQEAAXp_RQAClxQAAnmaGEtOsbVbM13tniQE",
                "CAACAgIAAxkBAAPpYn7LsjgOH0OSJFBGx6WoIIKr_vcAAmQZAAJgRSBL_cLL_Nrl4OskBA",
                "CAACAgIAAxkBAAICCWKCCLoO6Itf6HSKKGedTPzbyeioAAJQFAACey0pSznSfTz0daK-JAQ",
                "CAACAgIAAxkBAAICCmKCCN_lePGRwqFYK4cPGBD4k_lpAAJcGQACmGshS9K8iR0VSuDVJAQ",
        };
        return new InputFile(stickers[new SplittableRandom().nextInt(stickers.length)]);
    }
}
