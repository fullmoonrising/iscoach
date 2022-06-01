package com.telegram.folobot.service.handlers;

import com.telegram.folobot.ChatId;
import com.telegram.folobot.dto.FoloPidorDto;
import com.telegram.folobot.service.FoloPidorService;
import com.telegram.folobot.service.FoloUserService;
import com.telegram.folobot.service.MessageService;
import com.telegram.folobot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

import static com.telegram.folobot.ChatId.isAndrew;

@Component
@RequiredArgsConstructor
public class ContextIndependentHandler {
    private final FoloUserService foloUserService;
    private final FoloPidorService foloPidorService;
    private final MessageService messageService;
    private final UserService userService;

    public void handle(Update update) {
        //Добавление фолопользователя в бд
        addFoloUser(update);

        //Пересылка личных сообщений в спецчат
        forwardPrivate(update);
    }

    /**
     * Залоггировать пользователя
     *
     * @param update {@link Update}
     */
    private void addFoloUser(Update update) {
        Message message = update.getMessage();
        if (Objects.isNull(message.getIsAutomaticForward()) || !message.getIsAutomaticForward()) {
            User user = message.getFrom();
            if (Objects.isNull(user) && !message.getNewChatMembers().isEmpty()) {
                user = message.getNewChatMembers().get(0);
            }
            if (!Objects.isNull(user)) {
                // Фолопользователь
                foloUserService.save(
                        foloUserService.findById(user.getId())
                                .setName(userService.getUserName(user)));
                // И фолопидор
                if (!message.isUserMessage() && !foloPidorService.existsById(message.getChatId(), user.getId())) {
                    foloPidorService.save(new FoloPidorDto(message.getChatId(), user.getId()));
                }
            }
        }
    }

    /**
     * Пересылка личных сообщений
     *
     * @param update {@link Update}
     */
    private void forwardPrivate(Update update) {
        if (update.hasMessage())
            if (update.getMessage().isUserMessage()) {
                messageService.forwardMessage(ChatId.getPOC_ID(), update);
            } else if (isAndrew(update.getMessage().getFrom())) {
                messageService.forwardMessage(ChatId.getANDREWSLEGACY_ID(), update);
            }
    }
}
