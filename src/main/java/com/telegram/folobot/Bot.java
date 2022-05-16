package com.telegram.folobot;

import com.telegram.folobot.domain.Folopidor;
import com.telegram.folobot.repos.FolopidorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

// Аннотация @Component необходима, чтобы класс распознавался Spring, как полноправный Bean
@Component
// Наследуемся от TelegramLongPollingBot - абстрактного класса Telegram API
public class Bot extends TelegramLongPollingBot {
    private final String FOLOCHAT_ID = "-1001439088515";
    private final String POC_ID = "-1001154453685";
    private final String ANDREW_ID = "146072069";

    // TODO брать из properties
    private String botUsername = "FoloNewsBot";
    private String botToken = "1355996036:AAGnc3B93ALckw6W5WGsZ1DN29y1bYat1cw";

    @Autowired
    private FolopidorRepo folopidorRepo;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) { return; }

        // TODO
        forwardPrivate(update);

        //Логгируем сообщения Андрея
//        if (update.getMessage().hasText() && isAndrew(update.getMessage().getFrom())) {
//            Folopidor folopidor = new Folopidor(update.getMessage().getText(), "Андрюшин журнал");
//            folopidorRepo.save(folopidor);
//        }

        //Действие в зависимости от содержимого update
        onAction(getAction(update), update);
    }

    /**
     * Определяет действие на основе приходящего Update
     * @param update пробрасывается из onUpdateReceived
     * @return {@link  Actions}
     */
    private Actions getAction(Update update) {
        // Не содержит сообщения
        if (!update.hasMessage()) {
            return Actions.UNDEFINED;
        }

        Message message = update.getMessage();

        if (message.hasText()) {
            String text = message.getText();
            // Команда
            if (message.getChat().isUserChat() && text.startsWith("/") ||
                    !message.getChat().isUserChat() && text.startsWith("/") && text.endsWith("@" + botUsername)) {
                return Actions.COMMAND;
            }
            // Ответ на обращение
            if (text.toLowerCase().contains("гурманыч") || text.toLowerCase().contains(botUsername.toLowerCase())) {
                return Actions.REPLY;
            }
        }
        // Пользователь зашел в чат
        if (!message.getNewChatMembers().isEmpty()) {
            return Actions.USERNEW;
        }
        // Пользователь покинул чат
        if (Objects.nonNull(message.getLeftChatMember())) {
            return Actions.USERLEFT;
        }
        return Actions.UNDEFINED;
    }

    /**
     * Действие в зависимости от содержимого {@link Update}
     * @param action {@link Actions}
     * @param update пробрасывается из onUpdateReceived
     */
    private void onAction(Actions action, Update update) {
        if (action == null || action == Actions.UNDEFINED) {
            return;
        }

        switch (action) {
            case COMMAND:
                onCommand(update);
                break;
            case REPLY:
                onReply(update);
                break;
            case USERNEW:
                onUserNew(update);
                break;
            case USERLEFT:
                onUserLeft(update);
                break;
        }
    }

    /**
     * Выполнение команды
     * @param update {@link Update}
     */
    private void onCommand(Update update) {
        Commands command = Commands.valueOfLabel(update.getMessage().getText().split("@")[0]);

        switch (command) {
            case SILENTSTREAM:
                sendSticker(getRandomSticker(), update);
        }

    }

    /**
     * Ответ на обращение
      * @param update {@link Update}
     */
    private void onReply(Update update) {
        if (update.getMessage().getText().toLowerCase().contains("гурманыч") ||
                update.getMessage().getText().toLowerCase().contains(botUsername.toLowerCase())) {
            String userName = getUserName(update.getMessage().getFrom());
            if (userName == null || userName.isEmpty()) {
                sendMessage("Привет, уважаемый фолофил!", update);
            } else if (isAndrew(update.getMessage().getFrom())) {
                sendMessage("Привет, моя сладкая бориспольская булочка!", update);
            } else {
                sendMessage("Привет, уважаемый фолофил " + userName + "!", update);
            }
        }
    }

    /**
     * Пользователь зашел в чат
     * @param update {@link Update}
     */
    private void onUserNew(Update update) {
        List<User> users = update.getMessage().getNewChatMembers();
        if (isAndrew(users.get(0))) {
            sendMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update);
        } else {
            if (Long.toString(update.getMessage().getChat().getId()).equals(FOLOCHAT_ID)) {
                sendMessage("Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                        + getUserName(users.get(0)) + "!", update);
            } else {
                sendMessage("Это не настоящий фолочат, " + getUserName(users.get(0)) + "!", update);
                sendMessage("настоящий тут: \nt.me/alexfolomkin", update, false);
            }
        }
    }

    /**
     * Пользователь покинул чат
      * @param update {@link Update}
     */
    private void onUserLeft(Update update) {
        User user = update.getMessage().getLeftChatMember();
        if (isAndrew(user)) {
            sendMessage("Сладкая бориспольская булочка покинула чат", update);
        } else {
            sendMessage("Куда же ты, " + getUserName(user) + " ! Не уходи!", update);
        }
    }

    //TODO
    private void forwardPrivate(Update update) {
        if (update.getMessage().isUserMessage()) {
            try {
                execute(SendMessage
                        .builder()
                        .chatId(POC_ID)
                        .text(update.getMessage().getFrom() + "\nСообщение:\n" + update.getMessage().getText())
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получение имени пользователя
     * @param user {@link User}
     * @return Имя пользователя
     */
    private String getUserName(User user) {
        try {
            if (isAndrew(user)) {
                return "Андрей";
            } else {
                return Stream.of(user.getFirstName(), user.getUserName())
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);
            }
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    /**
     * Определение является ли пользователь Андреем
     * @param user {@link User}
     * @return да/нет
     */
    private boolean isAndrew(User user) {
        return user != null && Long.toString(user.getId()).equals(ANDREW_ID);
    }

    /**
     * Отправить сообщение
     * @param text текст сообщения
     * @param update {@link Update}
     */
    private void sendMessage(String text, Update update) {
        if (text != null && !text.isEmpty()) {
            try {
                execute(SendMessage
                        .builder()
                        .chatId(Long.toString(update.getMessage().getChatId()))
                        .text(text)
                        .replyToMessageId(update.getMessage().getMessageId())
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Отправить сообщение буз реплая
     * @param text текст сообщения
     * @param update {@link Update}
     * @param reply да/нет
     */
    private void sendMessage(String text, Update update, boolean reply) {
        if (reply) {
            this.sendMessage(text, update);
        } else {
            if (text != null && !text.isEmpty()) {
                try {
                    execute(SendMessage
                            .builder()
                            .chatId(Long.toString(update.getMessage().getChatId()))
                            .text(text)
                            .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Отправить стикер
     * @param stickerFile {@link InputFile}
     * @param update {@link Update}
     */
    private void sendSticker(InputFile stickerFile, Update update) {
        if (stickerFile != null) {
            try {
                execute(SendSticker
                        .builder()
                        .chatId(Long.toString(update.getMessage().getChatId()))
                        .sticker(stickerFile)
                        .replyToMessageId(update.getMessage().getMessageId())
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получить случайный стикер (из гиф пака)
     * @return {@link InputFile}
     */
    private InputFile getRandomSticker() {
        String[] stickers = {
            "CAACAgIAAxkBAAICCGKCCI-Ff-uqMZ-y4e0YmQEAAXp_RQAClxQAAnmaGEtOsbVbM13tniQE",
            "CAACAgIAAxkBAAPpYn7LsjgOH0OSJFBGx6WoIIKr_vcAAmQZAAJgRSBL_cLL_Nrl4OskBA",
            "CAACAgIAAxkBAAICCWKCCLoO6Itf6HSKKGedTPzbyeioAAJQFAACey0pSznSfTz0daK-JAQ",
            "CAACAgIAAxkBAAICCmKCCN_lePGRwqFYK4cPGBD4k_lpAAJcGQACmGshS9K8iR0VSuDVJAQ",
        };
        return new InputFile(stickers[(int) (Math.random() * (3))]);
    }

    // Геттеры, которые необходимы для наследования от TelegramLongPollingBot
    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}
