package com.telegram.folobot;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.telegram.folobot.constants.ActionsEnum;
import com.telegram.folobot.constants.BotCommandsEnum;
import com.telegram.folobot.constants.NumTypeEnum;
import com.telegram.folobot.dto.FoloPidorDto;
import com.telegram.folobot.dto.FoloUserDto;
import com.telegram.folobot.service.FoloPidorService;
import com.telegram.folobot.service.FoloUserService;
import com.telegram.folobot.service.FoloVarService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.telegram.folobot.ChatId.*;
import static com.telegram.folobot.Utils.printExeptionMessage;

// TODO разбить на сервисы, очень большой
// TODO переделать application.properties на yaml

@Service
@RequiredArgsConstructor
public class Bot extends TelegramWebhookBot { //TODO библиотека sl4j для логгирования, sonar lint plugin для проверки
    @Value("${bot.username}")
    @Getter
    private String botUsername;
    @Value("${bot.token}")
    @Getter
    private String botToken;
    @Value("${bot.path}")
    @Getter
    private String botPath;

    private final FoloPidorService foloPidorService;
    private final FoloUserService foloUserService;
    private final FoloVarService foloVarService;

    /**
     * Пришел update от Telegram
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
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
                        .setName(getUserName(user)));
                // И фолопидор
                if (!message.isUserMessage() && !foloPidorService.existsById(message.getChatId(), user.getId())) {
                    foloPidorService.save(new FoloPidorDto(message.getChatId(), user.getId()));
                }
            }
        }
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
                    return onCommand(update);
                case USERMESSAGE:
                    return onUserMessage(update);
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
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onCommand(Update update) {
        BotCommandsEnum command = BotCommandsEnum.valueOfLabel(update.getMessage().getText().split("@")[0]);
        if (command != null) {
            sendChatTyping(update);
            switch (command) { // TODO обрабатывать /start
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
                Utils.getPeriodText(Period.between(LocalDate.of(2019, 11, 18), LocalDate.now())) +
                "*!", update);
    }

    private BotApiMethod<?> nofapTimer(Update update) {
        LocalDate noFapDate;
        Integer noFapCount = 0;
        // Фо устанавливает дату
        if (isFo(update.getMessage().getFrom())) {
            noFapDate = LocalDate.now();
            foloVarService.setLastFapDate(noFapDate);
        } else {
            noFapDate = foloVarService.getLastFapDate();
            noFapCount = foloVarService.getNoFapCount(update.getMessage().getChatId());
        }

        if (noFapDate.equals(LocalDate.now())) {
            return buildMessage("Все эти молоденькие няшные студенточки вокруг..." +
                    "\nСорвался \"Но Фап\" сегодня...", update);
        } else {
            return buildMessage("Для особо озабоченных в *" +
                    new RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT)
                            .format(noFapCount, "%spellout-ordinal-masculine") +
                    "* раз повторяю тут Вам, что я с *" +
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(new Locale("ru"))
                            .format(noFapDate) +
                    "* и до сих пор вот уже *" +
                    Utils.getPeriodText(Period.between(noFapDate, LocalDate.now())) +
                    "* твёрдо и уверенно держу \"Но Фап\".", update);
        }
    }

    /**
     * Определяет фолопидора дня. Если уже определен показывает кто
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> foloPidor(Update update) {
        Long chatid = update.getMessage().getChatId();
        if (!update.getMessage().isUserMessage()) {
            //Определяем дату и победителя предыдущего запуска
            LocalDate lastDate = foloVarService.getLastFolopidorDate(chatid);
            Long lastWinner = foloVarService.getLastFolopidorWinner(chatid);

            //Определяем либо показываем победителя
            if (Objects.isNull(lastWinner) || lastDate.isBefore(LocalDate.now())) {
                //Выбираем случайного
                FoloPidorDto foloPidor = foloPidorService.getRandom(chatid);

                //Обновляем счетчик
                foloPidorService.save(foloPidor.setScore(foloPidor.getScore() + 1));

                //Обновляем текущего победителя
                foloVarService.setLastFolopidorWinner(chatid, foloPidor.getId().getUserId()); //TODO Бага при пустой бд
                foloVarService.setLastFolopidorDate(chatid, LocalDate.now());

                //Поздравляем
                sendMessage(Texts.getSetup(), update);
                sendMessage(Texts.getPunch(getFoloUserNameLinked(foloPidor)), update);
            } else {
                return buildMessage("Фолопидор дня уже выбран, это *" +
                        getFoloUserName(foloPidorService.findById(chatid, lastWinner)) +
                        "*. Пойду лучше лампово попержу в диван", update);
            }

        } else {
            return buildMessage("Для меня вы все фолопидоры, " +
                    getFoloUserName(update.getMessage().getFrom()), update, true);
        }
        return null;
    }

    /**
     * Показывает топ фолопидоров
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> foloPidorTop(Update update) {
        if (!update.getMessage().isUserMessage()) {
            StringJoiner top = new StringJoiner("\n").add("Топ 10 *фолопидоров*:\n");
            List<FoloPidorDto> foloPidors = foloPidorService.getTop(update.getMessage().getChatId());
            for (int i = 0; i < foloPidors.size(); i++) {
                String place = switch (i) {
                    case 0 -> "\uD83E\uDD47";
                    case 1 -> "\uD83E\uDD48";
                    case 2 -> "\uD83E\uDD49";
                    default -> "\u2004*" + (i + 1) + "*.\u2004";
                };
                FoloPidorDto foloPidor = foloPidors.get(i);
                top.add(place + getFoloUserName(foloPidor) + " — _" +
                        Utils.getNumText(foloPidor.getScore(), NumTypeEnum.COUNT) + "_");
            }
            return buildMessage(top.toString(), update);
        } else {
            return buildMessage("Андрей - почетный фолопидор на все времена!", update);
        }
    }

    /**
     * Ответ на личное сообщение
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onUserMessage(Update update) {
        if (isAndrew(update.getMessage().getFrom()) &&
                new SplittableRandom().nextInt(100) < 7) {
            forwardMessage(ChatId.getPOC_ID(), sendMessage(Texts.getQuoteforAndrew(), update, true));
        }
        return null;
    }

    /**
     * Ответ на обращение
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onReply(Update update) {
        // Cообщение в чат
        String text = update.getMessage().getText().toLowerCase();
        if (text.contains("привет") || new SplittableRandom().nextInt(100) < 20) {
            String userName = getFoloUserName(update.getMessage().getFrom());
            sendChatTyping(update);
            if (isAndrew(update.getMessage().getFrom())) {
                return buildMessage("Привет, моя сладкая бориспольская булочка!", update, true);
            } else {
                return buildMessage("Привет, уважаемый фолофил " + userName + "!", update, true);
            }
        }
        return null;
    }

    /**
     * Пользователь зашел в чат
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onUserNew(Update update) {
        sendChatTyping(update);
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
            if (isFolochat(update.getMessage().getChat())) {
                return buildMessage("Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                        + getFoloUserName(user) + "!", update, true);
            } else {
                sendMessage("Это не настоящий фолочат, " + getFoloUserName(user) + "!", update);
                sendMessage("настоящий тут: \nt.me/alexfolomkin", update);
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
    private BotApiMethod<?> onUserLeft(Update update) {
        sendChatTyping(update);
        User user = update.getMessage().getLeftChatMember();
        if (isAndrew(user)) {
            return buildMessage("Сладкая бориспольская булочка покинула чат", update);
        } else {
            return buildMessage("Куда же ты, " + getFoloUserName(user) + "! Не уходи!", update);
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
                forwardMessage(ChatId.getPOC_ID(), update);
            } else if (isAndrew(update.getMessage().getFrom())) {
                forwardMessage(ChatId.getANDREWSLEGACY_ID(), update);
            }
    }

    private String getUserName(User user) {
        if (user != null) {
            return Stream.of(Stream.of(user.getFirstName(), user.getLastName())
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.joining(" ")),
                            user.getUserName())
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Получение имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    private String getFoloUserName(User user) {
        FoloUserDto foloUser = foloUserService.findById(user.getId());
        // По тэгу
        String userName = foloUser.getTag();
        if (userName.isEmpty()) userName = getUserName(user);
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloUser.getName();
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец";
        return userName;
    }

    /**
     * Получение имени фолопидора
     *
     * @param foloPidorDto {@link FoloPidorDto}
     * @return Имя фолопидора
     */
    private String getFoloUserName(FoloPidorDto foloPidorDto) {
        // По тэгу
        String userName = foloPidorDto.getTag();
        // По пользователю
        if (userName.isEmpty()) userName = getUserName(getUserById(foloPidorDto.getId().getUserId()));
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloPidorDto.getName();
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец";
        return userName;
    }

    private User getUserById(Long userid) {
        try {
            return execute(new GetChatMember(Long.toString(userid), userid)).getUser();
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
    private String getFoloUserNameLinked(User user) {
        return "[" + getFoloUserName(user) + "](tg://user?id=" + user.getId() + ")";
    }

    /**
     * Получение кликабельного имени фолопидора
     *
     * @param foloPidorDto {@link FoloPidorDto}
     * @return Имя фолопидора
     */
    private String getFoloUserNameLinked(FoloPidorDto foloPidorDto) {
        return "[" + getFoloUserName(foloPidorDto) + "](tg://user?id=" + foloPidorDto.getId().getUserId() + ")";
    }

    /**
     * Проверка что {@link User} это этот бот
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
     * @return {@link Message}
     */
    public Message sendMessage(String text, Update update) {
        try {
            return execute(buildMessage(text, update));
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
        return null;
    }

    /**
     * Отправить сообщение буз реплая
     *
     * @param text   текст сообщения
     * @param update {@link Update}
     * @param reply  да/нет
     * @return {@link Message}
     */
    private Message sendMessage(String text, Update update, boolean reply) {
        if (!reply) {
            return this.sendMessage(text, update);
        } else {
            try {
                return execute(buildMessage(text, update, reply));
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
        return null;
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

    private void forwardMessage(Long chatid, Message message) {
        if (message != null) {
            try {
                execute(ForwardMessage
                        .builder()
                        .chatId(Long.toString(chatid))
                        .messageId(message.getMessageId())
                        .fromChatId(Long.toString(message.getChatId()))
                        .build());
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
    }

    /**
     * Отправить статус "печатает"
     *
     * @param update {@link Update}
     */
    private void sendChatTyping(Update update) {
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
}
