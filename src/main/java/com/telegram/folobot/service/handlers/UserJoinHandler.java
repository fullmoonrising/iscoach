package com.telegram.folobot.service.handlers;

import com.telegram.folobot.service.MessageService;
import com.telegram.folobot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.telegram.folobot.ChatId.*;

@Component
@RequiredArgsConstructor
public class UserJoinHandler {
    private final MessageService messageService;
    private final UserService userService;

    /**
     * Пользователь зашел в чат
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    public BotApiMethod<?> handleJoin(Update update) {
        messageService.sendChatTyping(update);
        User user = update.getMessage().getNewChatMembers().get(0);
        if (isAndrew(user)) {
            return messageService
                    .buildMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update, true);
        } else if (isVitalik(user)) {
            messageService.sendMessage("Как же я горю сейчас", update);
            messageService.sendMessage("Слово мужчини", update);
        } else if (userService.isSelf(user)) {
            messageService.sendMessage("Привет, с вами я, сильный и незаурядный репер МС Фоломкин.", update);
            messageService.sendMessage("Спасибо, что вы смотрите мои замечательные видеоклипы.", update);
            messageService.sendMessage("Я читаю текст, вы слушаете текст", update);
        } else {
            if (isFolochat(update.getMessage().getChat())) {
                return messageService
                        .buildMessage("Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                        + userService.getFoloUserName(user) + "!", update, true);
            } else {
                messageService.sendMessage("Это не настоящий фолочат, " +
                        userService.getFoloUserName(user) + "!", update);
                messageService.sendMessage("настоящий тут: \nt.me/alexfolomkin", update);
            }
        }
        return null;
    }

    /**
     * Пользователь покинул чат
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    public BotApiMethod<?> handleLeft(Update update) {
        messageService.sendChatTyping(update);
        User user = update.getMessage().getLeftChatMember();
        if (isAndrew(user)) {
            return messageService.buildMessage("Сладкая бориспольская булочка покинула чат", update);
        } else {
            return messageService
                    .buildMessage("Куда же ты, " + userService.getFoloUserName(user) + "! Не уходи!", update);
        }
    }
}
