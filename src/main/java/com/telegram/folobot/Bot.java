package com.telegram.folobot;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloUser;
import com.telegram.folobot.domain.FoloVar;
import com.telegram.folobot.enums.Actions;
import com.telegram.folobot.enums.BotCommands;
import com.telegram.folobot.enums.NumType;
import com.telegram.folobot.enums.VarType;
import com.telegram.folobot.repos.FoloPidorRepo;
import com.telegram.folobot.repos.FoloUserRepo;
import com.telegram.folobot.repos.FoloVarRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Аннотация @Component необходима, чтобы класс распознавался Spring, как полноправный Bean
@Component
// Наследуемся от TelegramLongPollingBot - абстрактного класса Telegram API
public class Bot extends TelegramLongPollingBot {
    private final Long FOLOCHAT_ID = -1001439088515L;
    private final Long POC_ID = -1001154453685L;
    private final Long ANDREW_ID = 146072069L;
    private final Long MY_ID = 50496196L;

    // TODO брать из properties
    private String botUsername = "FoloNewsBot";
    private String botToken = "1355996036:AAGnc3B93ALckw6W5WGsZ1DN29y1bYat1cw";

    @Autowired
    private FoloPidorRepo foloPidorRepo;
    @Autowired
    private FoloUserRepo foloUserRepo;
    @Autowired
    private FoloVarRepo foloVarRepo;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        //Добавление фолопользователя в бд
        addFoloUser(update);

        //TODO при атаке виталика засрать ему личку

        //Пересылка личных сообщений в спецчат
        forwardPrivate(update);

        //Действие в зависимости от содержимого update
        onAction(getAction(update), update);
    }

    /**
     * Залоггировать пользователя
     *
     * @param update {@link Update}
     */
    private void addFoloUser(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (!isChatMessage(message)) {
                User user = message.getFrom();
                if (user == null && !message.getNewChatMembers().isEmpty()) {
                    user = message.getNewChatMembers().get(0);
                }
                if (user != null) {
                    // Фолопользователь
                    if (foloUserRepo.findById(user.getId()).isEmpty()) {
                        foloUserRepo.save(new FoloUser(user.getId(), getUserName(user)));
                    }
                    // И фолопидор
                    if (!message.isUserMessage() && getFoloPidor(message.getChatId(), user.getId()).isNew()) {
                        foloPidorRepo.save(new FoloPidor(message.getChatId(), user.getId()));
                    }
                }
            }
        }
    }

    /**
     * Определение является ли сообщение постом канала
     *
     * @param message {@link  Message}
     * @return да/нет
     */
    private boolean isChatMessage(Message message) {
        try {
            return message.getIsAutomaticForward();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Определяет действие на основе приходящего Update
     *
     * @param update {@link Update} пробрасывается из onUpdateReceived
     * @return {@link Actions}
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
        BotCommands command = BotCommands.valueOfLabel(update.getMessage().getText().split("@")[0]);
        if (command != null) {
            switch (command) {
                case SILENTSTREAM:
                    sendSticker(getRandomSticker(), update);
                    break;
                case FREELANCE:
                    frelanceTimer(update);
                    break;
                case NOFAP:
                    nofapTimer(update);
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
     * Подсчет времени прошедшего с дня F
     *
     * @param update {@link Update}
     */
    private void frelanceTimer(Update update) {
        sendMessage("18 ноября 2019 года я уволился с завода по своему желанию.\n" +
                "С тех пор я стремительно вхожу в IT вот уже\n*" +
                Utils.getDateText(Period.between(LocalDate.of(2019, 11, 18), LocalDate.now())) +
                "*!", update);
    }

    private void nofapTimer(Update update) {
        sendMessage("Для особо озабоченных в десятый раз повторяю тут Вам, " +
                "что я с Нового 2020 Года и до сих пор вот уже *" +
                Utils.getDateText(Period.between(LocalDate.of(2020, 01, 01), LocalDate.now())) +
                "* твёрдо и уверенно держу \"Но Фап\".", update);
    }


    /**
     * Определяет фолопидора дня. Если уже определен показывает кто
     *
     * @param update {@link Update}
     */
    private void foloPidor(Update update) {
        Long chatid = update.getMessage().getChatId();
        if (chatid.equals(FOLOCHAT_ID)) {
            //Определяем дату и победителя предыдущего запуска
            LocalDate lastDate = getLastFolopidorDate(chatid);
            Long lastWinner = getLastFolopidorWinner(chatid);

            //Определяем либо показываем победителя
            if (lastWinner == null || lastDate.isBefore(LocalDate.now())) {
                //Выбираем случайного
                FoloPidor folopidor = getFoloPidor(chatid);

                //Обновляем счетчик
                folopidor.setScore(folopidor.getScore() + 1);
                foloPidorRepo.save(folopidor);

                //Обновляем текущего победителя
                setLastFolopidorWinner(chatid, folopidor.getUserid());
                setLastFolopidorDate(chatid, LocalDate.now());

                //Поздравляем
                sendMessage(Text.getSetup(), update, false);
                sendMessage(Text.getPunch(getUserName(folopidor)), update, false);
            } else {
                sendMessage("Фолопидор дня уже выбран, это *" + getUserName(getFoloPidor(chatid, lastWinner)) +
                        "*. Пойду лучше лампово попержу в диван", update);
            }

        } else {
            sendMessage("Для меня вы все фолопидоры, " +
                    getUserName(update.getMessage().getFrom()), update, true);
        }
    }

    /**
     * Дата последнего определения фолопидора
     *
     * @param chatid ID чата
     * @return {@link LocalDate}
     */
    public LocalDate getLastFolopidorDate(Long chatid) {
        List<FoloVar> foloVars = foloVarRepo.findByChatidAndType(chatid, VarType.LAST_FOLOPIDOR_DATE.name());
        return !foloVars.isEmpty() ? LocalDate.parse(foloVars.get(0).getValue()) : LocalDate.parse("1900-01-01");
    }

    /**
     * Сохранить дату последнего опрделения фолопидора
     *
     * @param chatid
     * @param value
     */
    public void setLastFolopidorDate(Long chatid, LocalDate value) {
        foloVarRepo.save(new FoloVar(chatid, VarType.LAST_FOLOPIDOR_DATE.name(), value.toString()));
    }

    /**
     * Последний фолопидор
     *
     * @param chatid
     * @return
     */
    public Long getLastFolopidorWinner(Long chatid) {
        List<FoloVar> foloVars = foloVarRepo.findByChatidAndType(chatid, VarType.LAST_FOLOPIDOR_USERID.name());
        return !foloVars.isEmpty() ? Long.parseLong(foloVars.get(0).getValue()) : null;
    }

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatid
     * @param value
     */
    public void setLastFolopidorWinner(Long chatid, Long value) {
        foloVarRepo.save(new FoloVar(chatid, VarType.LAST_FOLOPIDOR_USERID.name(), Long.toString(value)));
    }

    /**
     * Получение объекта фолопидора. Если не найден - возвращает пустого фолопидора
     *
     * @param chatid
     * @param userid
     * @return {@link FoloPidor}
     */
    public FoloPidor getFoloPidor(Long chatid, Long userid) {
        List<FoloPidor> foloPidors = foloPidorRepo.findByChatidAndUserid(chatid, userid);
        if (!foloPidors.isEmpty()) {
            return foloPidors.get(0);
        } else {
            FoloPidor foloPidor = new FoloPidor(chatid, userid);
            foloPidor.setNew(true);
            return foloPidor;
        }
    }

    /**
     * Выбор случайного фолопидора
     *
     * @param chatid
     * @return {@link FoloPidor}
     */
    public FoloPidor getFoloPidor(Long chatid) {
        //Получаем список фолопидоров для чата
        List<FoloPidor> foloPidors = foloPidorRepo.findByChatid(chatid);
        //Выбираем случайного
        return foloPidors.get((int) (Math.random() * foloPidors.size()));
    }

    /**
     * Показывает топ фолопидоров
     *
     * @param update {@link Update}
     */
    private void foloPidorTop(Update update) {
        if (update.getMessage().getChatId().equals(FOLOCHAT_ID)) {
            StringJoiner top = new StringJoiner("\n").add("Топ 10 *фолопидоров*:\n");
            List<FoloPidor> foloPidors =
                    foloPidorRepo.findByChatid(update.getMessage().getChatId()).stream()
                            .sorted(Comparator.comparingInt(FoloPidor::getScore).reversed())
                            .filter(FoloPidor::hasScore)
                            .limit(10)
                            .collect(Collectors.toList());
            for (int i = 0; i < foloPidors.size(); i++) {
                String place;
                switch (i) {
                    case 0:
                        place = "\uD83E\uDD47";
                        break;
                    case 1:
                        place = "\uD83E\uDD48";
                        break;
                    case 2:
                        place = "\uD83E\uDD49";
                        break;
                    default:
                        place = "  *" + (i + 1) + "*.  ";
                }
                FoloPidor foloPidor = foloPidors.get(i);
                top.add(place + getUserName(foloPidor) + " — _" +
                        Utils.getNumText(foloPidor.getScore(), NumType.COUNT) + "_");
            }
            sendMessage(top.toString(), update);
        } else {
            sendMessage("Андрей - почетный фолопидор на все времена!", update);
        }
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
                sendMessage("Привет, уважаемый фолофил!", update, true);
            } else if (isAndrew(update.getMessage().getFrom())) {
                sendMessage("Привет, моя сладкая бориспольская булочка!", update, true);
            } else {
                sendMessage("Привет, уважаемый фолофил " + userName + "!", update, true);
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
            sendMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update, true);
        } else {
            if (update.getMessage().getChat().getId().equals(FOLOCHAT_ID)) {
                sendMessage("Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                        + getUserName(users.get(0)) + "!", update, true);
            } else {
                sendMessage("Это не настоящий фолочат, " + getUserName(users.get(0)) + "!", update);
                sendMessage("настоящий тут: \nt.me/alexfolomkin", update);
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
            sendMessage("Куда же ты, " + getUserName(user) + "! Не уходи!", update);
        }
    }

    //TODO разобраться как форвардить

    /**
     * Пересылка личных сообщений
     *
     * @param update {@link Update}
     */
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
                if ((update.getMessage().getFrom().getId()).equals(ANDREW_ID) && new Random().nextInt(10) == 5) {
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
     * Получение имени фолопидора
     *
     * @param foloPidor {@link FoloPidor}
     * @return Имя фолопидора
     */
    private String getUserName(FoloPidor foloPidor) {
        try {
            return !foloPidor.getTag().isEmpty() ? foloPidor.getTag() :
                    getUserName(execute(new GetChatMember(Long.toString(foloPidor.getChatid()),
                            foloPidor.getUserid())).getUser());
        } catch (TelegramApiException ignored) {
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
                        .parseMode(ParseMode.MARKDOWN)
                        .chatId(Long.toString(update.getMessage().getChatId()))
                        .text(text)
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
        if (!reply) {
            this.sendMessage(text, update);
        } else {
            if (text != null && !text.isEmpty()) {
                try {
                    execute(SendMessage
                            .builder()
                            .parseMode(ParseMode.MARKDOWN)
                            .chatId(Long.toString(update.getMessage().getChatId()))
                            .text(text)
                            .replyToMessageId(update.getMessage().getMessageId())
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
        return new InputFile(stickers[(int) (Math.random() * stickers.length)]);
    }

    // Геттеры, которые необходимы для наследования от TelegramLongPollingBot
    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}
