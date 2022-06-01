package com.telegram.folobot;

import com.telegram.folobot.constants.ActionsEnum;
import com.telegram.folobot.service.MessageService;
import com.telegram.folobot.service.UserService;
import com.telegram.folobot.service.handlers.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

// TODO переделать application.properties на yaml

@Service
public class FoloBot extends TelegramWebhookBot { //TODO библиотека sl4j для логгирования, sonar lint plugin для проверки
    @Value("${bot.username}")
    @Getter
    private String botUsername;
    @Value("${bot.token}")
    @Getter
    private String botToken;
    @Value("${bot.path}")
    @Getter
    private String botPath;

    private final ContextIndependentHandler ciHandler;
    private final CommandHandler commandHandler;
    private final UserMessageHandler userMessageHandler;
    private final ReplyHandler replyHandler;
    private final UserJoinHandler userJoinHandler;

    /**
     * Конструктор с инъекцией обработчиков и передачей объекта бота сервисам
     * @param ciHandler {@link ContextIndependentHandler}
     * @param commandHandler {@link CommandHandler}
     * @param userMessageHandler {@link UserMessageHandler}
     * @param replyHandler {@link ReplyHandler}
     * @param userJoinHandler {@link UserJoinHandler}
     */
    public FoloBot(ContextIndependentHandler ciHandler,
                   CommandHandler commandHandler,
                   UserMessageHandler userMessageHandler,
                   ReplyHandler replyHandler,
                   UserJoinHandler userJoinHandler,
                   MessageService messageService,
                   UserService userService) {
        this.ciHandler = ciHandler;
        this.commandHandler = commandHandler;
        this.userMessageHandler = userMessageHandler;
        this.replyHandler = replyHandler;
        this.userJoinHandler = userJoinHandler;
        messageService.setFoloBot(this);
        userService.setFoloBot(this);
    }

    /**
     * Пришел update от Telegram
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            //Выполнение независящих от контекста действий
            ciHandler.handle(update);

            //Действие в зависимости от содержимого update
            return onAction(getAction(update), update);
        }
        return null;
    }

    /**
     * Определяет действие на основе приходящего Update
     *
     * @param update {@link Update} пробрасывается из onUpdateReceived
     * @return {@link ActionsEnum}
     */
    private ActionsEnum getAction(Update update) {
        Message message = update.getMessage();

        // Команда
        if (message.hasText()) {
            if (message.getChat().isUserChat() && message.getText().startsWith("/") ||
                    !message.getChat().isUserChat() && message.getText().startsWith("/") &&
                            message.getText().endsWith("@" + botUsername)) {
                return ActionsEnum.COMMAND;
            }
        }
        // Личное сообщение
        if (message.isUserMessage()) {
            return ActionsEnum.USERMESSAGE;
        }
        // Ответ на обращение
        if (message.hasText()) {
            if (message.getText().toLowerCase().contains("гурманыч") ||
                    message.getText().toLowerCase().contains(botUsername.toLowerCase())) {
                return ActionsEnum.REPLY;
            }
        }
        // Пользователь зашел в чат
        if (!message.getNewChatMembers().isEmpty()) {
            return ActionsEnum.USERNEW;
        }
        // Пользователь покинул чат
        if (Objects.nonNull(message.getLeftChatMember())) {
            return ActionsEnum.USERLEFT;
        }
        return ActionsEnum.UNDEFINED;
    }

    /**
     * Действие в зависимости от содержимого {@link Update}
     *
     * @param action {@link ActionsEnum}
     * @param update пробрасывается из onUpdateReceived
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onAction(ActionsEnum action, Update update) {
        if (action != null && action != ActionsEnum.UNDEFINED) {

            switch (action) {
                case COMMAND:
                    return commandHandler.handle(update);
                case USERMESSAGE:
                    return userMessageHandler.handle(update);
                case REPLY:
                    return replyHandler.handle(update); //TODO обрабатывать фолопидор, алые паруса и восточная любовь
                case USERNEW:
                    return userJoinHandler.handleJoin(update);
                case USERLEFT:
                    return userJoinHandler.handleLeft(update);
            }
        }
        return null;
    }
}
