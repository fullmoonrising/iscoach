package com.telegram.folobot;

import com.telegram.folobot.domain.*;
import com.telegram.folobot.enums.*;
import com.telegram.folobot.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.methods.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.telegram.folobot.Utils.printExeptionMessage;

// Аннотация @Component необходима, чтобы класс распознавался Spring, как полноправный Bean
@Component
// Наследуемся от TelegramLongPollingBot - абстрактного класса Telegram API
public class Bot extends TelegramWebhookBot {
    private final Long FOLOCHAT_ID = -1001439088515L;
    private final Long POC_ID = -1001154453685L;
    private final Long ANDREWSLEGACY_ID = -1001210743498L;
    private final Long ANDREW_ID = 146072069L;
    private final Long VITALIK_ID = 800522859L;
    private final Long MY_ID = 50496196L;

    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.path}")
    private String botPath;

    @Autowired
    private FoloPidorRepo foloPidorRepo;
    @Autowired
    private FoloUserRepo foloUserRepo;
    @Autowired
    private FoloVarRepo foloVarRepo;

    /**
     * Пришел update от Telegram
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod<?>}
     */
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            //Добавление фолопользователя в бд
            addFoloUser(update);

            //Пересылка личных сообщений в спецчат
            forwardPrivate(update);

            //Действие в зависимости от содержимого update
            return onAction(getAction(update), update);
        }
        return null;
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
                    foloUserRepo.save(new FoloUser(user.getId(), getUserName(user)));
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
    private BotApiMethod<?> onAction(Actions action, Update update) {
        if (action != null && action != Actions.UNDEFINED) {

            // Отправка чат статуса
            SendChatTyping(update);

            switch (action) {
                case COMMAND:
                    return onCommand(update);
                case REPLY:
                    return onReply(update);
                case USERNEW:
                    return onUserNew(update);
                case USERLEFT:
                    return onUserLeft(update);
            }
        }
        return null;
    }

    /**
     * Выполнение команды
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> onCommand(Update update) {
        BotCommands command = BotCommands.valueOfLabel(update.getMessage().getText().split("@")[0]);
        if (command != null) {
            switch (command) {
                case SILENTSTREAM:
                    sendSticker(getRandomSticker(), update); //TODO возвращать BotApiMethod
                    break;
                case FREELANCE:
                    return frelanceTimer(update);
                case NOFAP:
                    return nofapTimer(update);
                case FOLOPIDOR:
                    return foloPidor(update);
                case FOLOPIDORTOP:
                    return foloPidorTop(update);
            }
        }
        return null;
    }

    /**
     * Подсчет времени прошедшего с дня F
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> frelanceTimer(Update update) {
        return buildMessage("18 ноября 2019 года я уволился с завода по своему желанию.\n" +
                "С тех пор я стремительно вхожу в IT вот уже\n*" +
                Utils.getDateText(Period.between(LocalDate.of(2019, 11, 18), LocalDate.now())) +
                "*!", update);
    }

    private BotApiMethod<?> nofapTimer(Update update) {
        return buildMessage("Для особо озабоченных в десятый раз повторяю тут Вам, " +
                "что я с Нового 2020 Года и до сих пор вот уже *" +
                Utils.getDateText(Period.between(LocalDate.of(2020, 1, 1), LocalDate.now())) +
                "* твёрдо и уверенно держу \"Но Фап\".", update);
    }


    /**
     * Определяет фолопидора дня. Если уже определен показывает кто
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> foloPidor(Update update) {
        Long chatid = update.getMessage().getChatId();
//        if (chatid.equals(FOLOCHAT_ID)) {
        if (!update.getMessage().isUserMessage()) {
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
                sendMessage(Text.getSetup(), update);
                sendMessage(Text.getPunch(getUserName(folopidor)), update);
            } else {
                return buildMessage("Фолопидор дня уже выбран, это *" + getUserName(getFoloPidor(chatid, lastWinner)) +
                        "*. Пойду лучше лампово попержу в диван", update);
            }

        } else {
            return buildMessage("Для меня вы все фолопидоры, " +
                    getUserName(update.getMessage().getFrom()), update, true);
        }
        return null;
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
     * Сохранить дату последнего определения фолопидора
     *
     * @param chatid ID чата
     * @param value  Дата
     */
    public void setLastFolopidorDate(Long chatid, LocalDate value) {
        foloVarRepo.save(new FoloVar(chatid, VarType.LAST_FOLOPIDOR_DATE.name(), value.toString()));
    }

    /**
     * Последний фолопидор
     *
     * @param chatid ID чата
     * @return {@link Long} userid
     */
    public Long getLastFolopidorWinner(Long chatid) {
        List<FoloVar> foloVars = foloVarRepo.findByChatidAndType(chatid, VarType.LAST_FOLOPIDOR_USERID.name());
        return !foloVars.isEmpty() ? Long.parseLong(foloVars.get(0).getValue()) : null;
    }

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatid ID чата
     * @param value  {@link Long} userid
     */
    public void setLastFolopidorWinner(Long chatid, Long value) {
        foloVarRepo.save(new FoloVar(chatid, VarType.LAST_FOLOPIDOR_USERID.name(), Long.toString(value)));
    }

    /**
     * Получение объекта фолопидора. Если не найден - возвращает пустого фолопидора
     *
     * @param chatid ID чата
     * @param userid ID фолопидора
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
     * @param chatid ID чата
     * @return {@link FoloPidor}
     */
    public FoloPidor getFoloPidor(Long chatid) {
        //Получаем список фолопидоров для чата
        List<FoloPidor> foloPidors = foloPidorRepo.findByChatid(chatid);
        //Выбираем случайного
        return foloPidors.get(new SplittableRandom().nextInt(foloPidors.size()));
    }

    /**
     * Показывает топ фолопидоров
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> foloPidorTop(Update update) {
        if (!update.getMessage().isUserMessage()) {
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
                        place = "\u00A0*" + (i + 1) + "*.\u00A0";
                }
                FoloPidor foloPidor = foloPidors.get(i);
                top.add(place + getUserName(foloPidor) + " — _" +
                        Utils.getNumText(foloPidor.getScore(), NumType.COUNT) + "_");
            }
            return buildMessage(top.toString(), update);
        } else {
            return buildMessage("Андрей - почетный фолопидор на все времена!", update);
        }
    }

    /**
     * Ответ на обращение
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> onReply(Update update) {
        // Cообщение в чат
        if (!update.getMessage().isUserMessage()) {
            String text = update.getMessage().getText().toLowerCase();
            if ((text.contains("гурманыч") || text.contains(botUsername.toLowerCase())) &&
                    (text.contains("привет") || new SplittableRandom().nextInt(100) < 20)) {
                String userName = getUserName(update.getMessage().getFrom());
                if (userName == null || userName.isEmpty()) {
                    return buildMessage("Привет, уважаемый фолофил!", update, true);
                } else if (isAndrew(update.getMessage().getFrom())) {
                    return buildMessage("Привет, моя сладкая бориспольская булочка!", update, true);
                } else {
                    return buildMessage("Привет, уважаемый фолофил " + userName + "!", update, true);
                }
            }
        } else { //Личное сообщение
            if (isAndrew(update.getMessage().getFrom()) && new SplittableRandom().nextInt(100) < 20) {
                return buildMessage("", update, true); //TODO
            }
        }
        return null;
    }

    /**
     * Пользователь зашел в чат
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> onUserNew(Update update) {
        User user = update.getMessage().getNewChatMembers().get(0);
        if (isAndrew(user)) {
            return buildMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update, true);
        } else if (isVitalik(user)) {
            sendMessage("Как же я горю сейчас", update);
            sendMessage("Слово мужчини", update);
        } else if (isSelf(user)) {
            sendMessage("Привет, с вами я, сильный и незаурядный репер МС Фоломкин.", update);
            sendMessage("Спасибо, что вы смотрите мои замечательные видеоклипы.", update);
            sendMessage("Я читаю текст, вы слушаете текст", update);
        } else {
            if (update.getMessage().getChat().getId().equals(FOLOCHAT_ID)) {
                return buildMessage("Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                        + getUserName(user) + "!", update, true);
            } else {
                sendMessage("Это не настоящий фолочат, " + getUserName(user) + "!", update);
                sendMessage("настоящий тут: \nt.me/alexfolomkin", update);
            }
        }
        return null;
    }

    /**
     * Пользователь покинул чат
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> onUserLeft(Update update) {
        User user = update.getMessage().getLeftChatMember();
        if (isAndrew(user)) {
            return buildMessage("Сладкая бориспольская булочка покинула чат", update);
        } else {
            return buildMessage("Куда же ты, " + getUserName(user) + "! Не уходи!", update);
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
                forwardMessage(POC_ID, update);
            } else if (update.getMessage().getFrom().getId().equals(ANDREW_ID)) {
                forwardMessage(ANDREWSLEGACY_ID, update);
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
     * Получение кликабельного имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    private String getUserNameLinked(User user) {
        return "[" + getUserName(user) + "](tg://user?id=" + user.getId() + ")";
    }

    /**
     * Получение кликабельного имени фолопидора
     *
     * @param foloPidor {@link FoloPidor}
     * @return Имя фолопидора
     */
    private String getUserNameLinked(FoloPidor foloPidor) {
        return "[" + getUserName(foloPidor) + "](tg://user?id=" + foloPidor.getUserid() + ")";
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
     * Определение является ли пользователь Виталиком
     *
     * @param user {@link User}
     * @return да/нет
     */
    private boolean isVitalik(User user) {
        return user != null && user.getId().equals(VITALIK_ID);
    }

    /**
     * Определение является ли пользователь Виталиком
     *
     * @param user {@link User}
     * @return да/нет
     */
    private boolean isSelf(User user) {
        try {
            return user != null && user.getId().equals(getMe().getId());
        } catch (TelegramApiException e) {
            return false;
        }
    }

    /**
     * Собрать объект {@link SendMessage}
     *
     * @param text   Текст сообщения
     * @param update {@link Update}
     * @return {@link SendMessage}
     */
    private SendMessage buildMessage(String text, Update update) {
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
    private SendMessage buildMessage(String text, Update update, boolean reply) {
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
            execute(SendMessage
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
     */
    private void sendMessage(String text, Update update) {
        try {
            execute(buildMessage(text, update));
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
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
            try {
                execute(buildMessage(text, update, reply));
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
    }

    //TODO javadoc + возвращаться должно BotApiMethod
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
    private void sendSticker(InputFile stickerFile, Update update) {
        if (stickerFile != null) {
            try {
                execute(buildSticker(stickerFile, update));
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
    private void forwardMessage(Long chatid, Update update) {
        try {
            execute(ForwardMessage
                    .builder()
                    .chatId(Long.toString(chatid))
                    .messageId(update.getMessage().getMessageId())
                    .fromChatId(Long.toString(update.getMessage().getChatId()))
                    .build());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
    }

    /**
     * Отправить статус "печатает"
     *
     * @param update {@link Update}
     */
    private void SendChatTyping(Update update) {
        try {
            execute(SendChatAction
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
    private InputFile getRandomSticker() {
        String[] stickers = {
                "CAACAgIAAxkBAAICCGKCCI-Ff-uqMZ-y4e0YmQEAAXp_RQAClxQAAnmaGEtOsbVbM13tniQE",
                "CAACAgIAAxkBAAPpYn7LsjgOH0OSJFBGx6WoIIKr_vcAAmQZAAJgRSBL_cLL_Nrl4OskBA",
                "CAACAgIAAxkBAAICCWKCCLoO6Itf6HSKKGedTPzbyeioAAJQFAACey0pSznSfTz0daK-JAQ",
                "CAACAgIAAxkBAAICCmKCCN_lePGRwqFYK4cPGBD4k_lpAAJcGQACmGshS9K8iR0VSuDVJAQ",
        };
        return new InputFile(stickers[new SplittableRandom().nextInt(stickers.length)]);
    }

    // Геттеры, которые необходимы для наследования
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }
}
