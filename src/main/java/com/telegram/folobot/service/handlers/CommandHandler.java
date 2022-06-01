package com.telegram.folobot.service.handlers;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.telegram.folobot.FoloBot;
import com.telegram.folobot.Utils;
import com.telegram.folobot.constants.BotCommandsEnum;
import com.telegram.folobot.constants.NumTypeEnum;
import com.telegram.folobot.dto.FoloPidorDto;
import com.telegram.folobot.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

import static com.telegram.folobot.ChatId.isFo;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final FoloVarService foloVarService;
    private final FoloPidorService foloPidorService;
    private final MessageService messageService;
    private final UserService userService;
    private final TextService textService;

    /**
     * Выполнение команды
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    public BotApiMethod<?> handle(Update update) {

        BotCommandsEnum command = BotCommandsEnum.valueOfLabel(update.getMessage().getText().split("@")[0]);
        if (command != null) {
            messageService.sendChatTyping(update);
            switch (command) { // TODO обрабатывать /start
                case SILENTSTREAM:
                    messageService.sendSticker(messageService.getRandomSticker(), update);
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
        return messageService.buildMessage("18 ноября 2019 года я уволился с завода по своему желанию.\n" +
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
            return messageService.buildMessage("Все эти молоденькие няшные студенточки вокруг..." +
                    "\nСорвался \"Но Фап\" сегодня...", update);
        } else {
            return messageService.buildMessage("Для особо озабоченных в *" +
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
                messageService.sendMessage(textService.getSetup(), update);
                messageService.sendMessage(textService.getPunch(userService
                        .getFoloUserNameLinked(foloPidor)), update);
            } else {
                return messageService.buildMessage("Фолопидор дня уже выбран, это *" +
                        userService.getFoloUserName(foloPidorService.findById(chatid, lastWinner)) +
                        "*. Пойду лучше лампово попержу в диван", update);
            }

        } else {
            return messageService.buildMessage("Для меня вы все фолопидоры, " +
                    userService.getFoloUserName(update.getMessage().getFrom()), update, true);
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
                top.add(place + userService.getFoloUserName(foloPidor) + " — _" +
                        Utils.getNumText(foloPidor.getScore(), NumTypeEnum.COUNT) + "_");
            }
            return messageService.buildMessage(top.toString(), update);
        } else {
            return messageService.buildMessage("Андрей - почетный фолопидор на все времена!", update);
        }
    }
}
