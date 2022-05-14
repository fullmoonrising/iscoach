package com.example.twitor;

import com.example.twitor.domain.Message;
import com.example.twitor.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

// Аннотация @Component необходима, чтобы класс распознавался Spring, как полноправный Bean
@Component
// Наследуемся от TelegramLongPollingBot - абстрактного класса Telegram API
public class Bot extends TelegramLongPollingBot {

    private String botUsername = "FoloNewsBot";

    private String botToken = "1355996036:AAGnc3B93ALckw6W5WGsZ1DN29y1bYat1cw";

    @Autowired
    private MessageRepo messageRepo;

    private enum Commands {
        SILENTSTREAM("/silentstream");

        public final String label;

        private Commands(String label) {
            this.label = label;
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String msgText = "";
        InputFile stickerFile = null;

        if (!update.hasMessage()) { return; }

        /* TODO
            форвардить все сообщения в личку или от андрея в отдельную группу
         */
        //Логгируем сообщения Андрея
        if (update.getMessage().hasText() && isAndrew(update.getMessage().getFrom())) {
            Message message = new Message(update.getMessage().getText(), "Андрюшин журнал");
            messageRepo.save(message);
        }

        //Основная логика TODO метод определяющий action
        if (update.getMessage().hasText()) {
            // TODO проверять что в личку или в чате по маске соманда + @ + имя бота
            if (update.getMessage().getText().startsWith(Commands.SILENTSTREAM.label)) {
                stickerFile = new InputFile("CAACAgIAAxkBAAPpYn7LsjgOH0OSJFBGx6WoIIKr_vcAAmQZAAJgRSBL_cLL_Nrl4OskBA");
            } else if (update.getMessage().getText().toLowerCase().contains("гурманыч") ||
                    update.getMessage().getText().toLowerCase().contains(botUsername.toLowerCase())) {
                msgText = onUpdateReply(update);
            }
        } else if (!update.getMessage().getNewChatMembers().isEmpty()) {
            msgText = onJoinReply(update);
        } else if (Objects.nonNull(update.getMessage().getLeftChatMember())) {
            msgText = onLeaveReply(update);
        }

        try {
            /* TODO
                Отсутствует сообщение на которое отвечаем
                отправлять сообщение типа "проказник X удалил свое сообщение"
             */
            if (!msgText.isEmpty()) {
                execute(SendMessage
                        .builder()
                        .chatId(Long.toString(update.getMessage().getChatId()))
                        .text(msgText)
                        .replyToMessageId(update.getMessage().getMessageId())
                        .build());
            } else if (stickerFile != null) {
                execute(SendSticker
                        .builder()
                        .chatId(Long.toString(update.getMessage().getChatId()))
                        .sticker(stickerFile)
                        .replyToMessageId(update.getMessage().getMessageId())
                        .build());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String onUpdateReply(Update update) {
        String userName = getUserName(update.getMessage().getFrom());
        if (userName == null || userName.isEmpty()) {
            return "Привет, уважаемый фолофил!";
        } else if (isAndrew(update.getMessage().getFrom())) {
            return "Привет, моя сладкая бориспольская булочка!";
        } else {
            return "Привет, уважаемый фолофил " + userName + "!";
        }
    }

    private String onJoinReply(Update update) {
        List<User> users = update.getMessage().getNewChatMembers();
        if (isAndrew(users.get(0))) {
            return "Наконец то ты вернулся, мой сладкий пирожочек Андрюша!";
        } else {
            return "Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                    + getUserName(users.get(0)) + "!";
        }
    }

    private String onLeaveReply(Update update) {
        User user = update.getMessage().getLeftChatMember();
        if (isAndrew(user)) {
            return "Сладкая бориспольская булочка покинула чат";
        } else {
            return "Куда же ты, " + getUserName(user) + " ! Не уходи!";
        }
    }

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

    private boolean isAndrew(User user) {
        String andrevId = "146072069";
        return user != null && Long.toString(user.getId()).equals(andrevId);
    }

    private InputFile getRandomSticker( ) { //TODO
        return null;
    }

    // Геттеры, которые необходимы для наследования от TelegramLongPollingBot
    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}
