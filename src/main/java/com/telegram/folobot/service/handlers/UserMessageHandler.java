package com.telegram.folobot.service.handlers;

import com.telegram.folobot.ChatId;
import com.telegram.folobot.FoloBot;
import com.telegram.folobot.service.MessageService;
import com.telegram.folobot.service.TextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.SplittableRandom;

import static com.telegram.folobot.ChatId.isAndrew;

@Component
@RequiredArgsConstructor
public class UserMessageHandler {
    private final MessageService messageService;
    private final TextService textService;

    /**
     * Ответ на личное сообщение
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    public BotApiMethod<?> handle(Update update) {
        if (isAndrew(update.getMessage().getFrom()) &&
                new SplittableRandom().nextInt(100) < 7) {
            messageService.forwardMessage(ChatId.getPOC_ID(),
                    messageService.sendMessage(textService.getQuoteforAndrew(), update, true));
        }
        return null;
    }
}
