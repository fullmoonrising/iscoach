package com.telegram.folobot;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloUser;
import com.telegram.folobot.enums.Actions;
import com.telegram.folobot.enums.Commands;
import com.telegram.folobot.repos.FoloPidorRepo;
import com.telegram.folobot.repos.FoloUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Аннотация @Component необходима, чтобы класс распознавался Spring, как полноправный Bean
@Component
// Наследуемся от TelegramLongPollingBot - абстрактного класса Telegram API
public class Bot extends TelegramLongPollingBot {
    private final long FOLOCHAT_ID = -1001439088515L;
    private final long POC_ID = -1001154453685L;
    private final long ANDREW_ID = 146072069;
    private final long MY_ID = 50496196;

    // TODO брать из properties
    private String botUsername = "FoloNewsBot";
    private String botToken = "1355996036:AAGnc3B93ALckw6W5WGsZ1DN29y1bYat1cw";

    @Autowired
    private FoloPidorRepo foloPidorRepo;
    @Autowired
    private FoloUserRepo foloUserRepo;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        //Добавление фолопользователя в бд
        addFoloUser(update);

        //Пересылка личных сообщений в спецчат
        forwardPrivate(update);

        //Действие в зависимости от содержимого update
        onAction(getAction(update), update);
    }

    private void addFoloUser(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            User user = message.getFrom();
            if (user == null && !message.getNewChatMembers().isEmpty()) {
                user = message.getNewChatMembers().get(0);
            }
            if (user != null) {
                // Фолопользователь
                Optional<FoloUser> foloUsers = foloUserRepo.findById(user.getId());
                if (foloUsers.isEmpty()) {
                    foloUserRepo.save(new FoloUser(user.getId(), getUserName(user)));
                }
                // И фолопидор
                if (!update.getMessage().isUserMessage()) {
                    List<FoloPidor> folopidors = foloPidorRepo.findByChatidAndUserid(message.getChatId(), user.getId());
                    if (folopidors.isEmpty()) {
                        foloPidorRepo.save(new FoloPidor(message.getChatId(), user.getId()));
                    }
                }
            }
        }
    }

    /**
     * Определяет действие на основе приходящего Update
     *
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
     *
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
     *
     * @param update {@link Update}
     */
    private void onCommand(Update update) {
        Commands command = Commands.valueOfLabel(update.getMessage().getText().split("@")[0]);
        if (command != null) {
            switch (command) { //TODO добавить команду по которой показывать сколько прошло с момента увольнения
                case SILENTSTREAM:
                    sendSticker(getRandomSticker(), update);
                    break;
                case FOLOPIDOR:
                    foloPidor(update);
                    break;
                case FOLOPIDORTOP:
                    foloPidorTop(update);
                    break;
            }
        }
    }

    /**
     * Определяет фолопидора дня. Если уже определен показывает кто
     *
     * @param update {@link Update}
     */
    private void foloPidor(Update update) { //пилот на тайне переписки TODO поменять на фолочат
        //TODO проверять был ли запущен
        if (update.getMessage().getChatId().equals(POC_ID)) {
            //Получаем список фолопидоров для чата
            List<FoloPidor> folopidors = foloPidorRepo.findByChatid(update.getMessage().getChatId());
            try {
                //Выбираем случайного
                FoloPidor folopidor = folopidors.get((int) (Math.random() * folopidors.size()));
                //Обновляем счетчик
                folopidor.setScore(folopidor.getScore() + 1);
                foloPidorRepo.save(folopidor);
                //Получаем инфу о нем
                String name = !folopidor.getTag().isEmpty() ? folopidor.getTag() :
                        getUserName(execute(new GetChatMember(Long.toString(update.getMessage().getChatId()),
                                folopidor.getUserid())).getUser());
                //Поздравляем
                sendMessage("Фолопидор дня - " + name, update); //TODO переписать текст
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else {
            sendMessage("Для меня вы все фолопидоры, " + getUserName(update.getMessage().getFrom()), update);
        }
    }

    /**
     * Показывает топ фолопидоров
     *
     * @param update {@link Update}
     */
    private void foloPidorTop(Update update) {
        sendMessage("Андрей - почетный фолопидор на все времена!", update); //TODO переписать текст
//        if (update.getMessage().getChatId().equals(POC_ID)) { //пилот на тайне переписки TODO поменять на фолочат
//
//        } else {
//            sendMessage("Андрей - почетный фолопидор на все времена!", update);
//        }
    }

    /**
     * Ответ на обращение
     *
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
     *
     * @param update {@link Update}
     */
    private void onUserNew(Update update) {
        List<User> users = update.getMessage().getNewChatMembers();
        if (isAndrew(users.get(0))) {
            sendMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update);
        } else {
            if (update.getMessage().getChat().getId().equals(FOLOCHAT_ID)) {
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
     *
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

    //TODO разобраться как форвардить
    private void forwardPrivate(Update update) {
        if (update.hasMessage() &&
                !(update.getMessage().getFrom().getId()).equals(MY_ID) &&
                update.getMessage().isUserMessage() &&
                update.getMessage().hasText()) {
            try {
                execute(SendMessage
                        .builder()
                        .chatId(Long.toString(POC_ID))
                        .text("UserID: " + update.getMessage().getFrom().getId() + "\n" +
                                "Name: " + getUserName(update.getMessage().getFrom()) +
                                "\nСообщение:\n" + update.getMessage().getText())
                        .build());
                //Ответ Андрею стикером с фо
                if ((update.getMessage().getFrom().getId()).equals(ANDREW_ID)) {
                    sendSticker(getRandomSticker(), update);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получение имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    private String getUserName(User user) {
        try {
            if (isAndrew(user)) {
                return "Андрей";
            } else {
                return Stream.of(Stream.of(user.getFirstName(), user.getLastName())
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.joining(" ")),
                                user.getUserName())
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
     *
     * @param user {@link User}
     * @return да/нет
     */
    private boolean isAndrew(User user) {
        return user != null && user.getId().equals(ANDREW_ID);
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
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
     *
     * @param text   текст сообщения
     * @param update {@link Update}
     * @param reply  да/нет
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
     *
     * @param stickerFile {@link InputFile}
     * @param update      {@link Update}
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
     *
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
