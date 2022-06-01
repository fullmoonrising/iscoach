package com.telegram.folobot.service.handlers;

import com.telegram.folobot.FoloBot;
import com.telegram.folobot.service.MessageService;
import com.telegram.folobot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.SplittableRandom;

import static com.telegram.folobot.ChatId.isAndrew;

@Component
@RequiredArgsConstructor
public class ReplyHandler {
    private final UserService userService;
    private final MessageService messageService;

    /**
     * Ответ на обращение
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    public BotApiMethod<?> handle(Update update) {
        // Cообщение в чат
        String text = update.getMessage().getText().toLowerCase();
        if (text.contains("привет") || new SplittableRandom().nextInt(100) < 20) {
            String userName = userService.getFoloUserName(update.getMessage().getFrom());
            messageService.sendChatTyping(update);
            if (isAndrew(update.getMessage().getFrom())) {
                return messageService
                        .buildMessage("Привет, моя сладкая бориспольская булочка!", update, true);
            } else {
                return messageService
                        .buildMessage("Привет, уважаемый фолофил " + userName + "!", update, true);
            }
        }
        return null;
    }
}
