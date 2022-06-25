package com.telegram.folobot.service.handlers

import com.ibm.icu.text.RuleBasedNumberFormat
import com.telegram.folobot.ChatId.Companion.ANDREW_ID
import com.telegram.folobot.ChatId.Companion.isFo
import com.telegram.folobot.Utils.getNumText
import com.telegram.folobot.Utils.getPeriodText
import com.telegram.folobot.constants.BotCommandsEnum
import com.telegram.folobot.constants.NumTypeEnum
import com.telegram.folobot.service.*
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@Component
class CommandHandler(
    private val foloVarService: FoloVarService,
    private val foloPidorService: FoloPidorService,
    private val messageService: MessageService,
    private val userService: UserService,
    private val textService: TextService,
) {

    /**
     * Выполнение команды
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handle(update: Update): BotApiMethod<*>? {
        val command = BotCommandsEnum.fromCommand(update.message.text.substringBefore("@"))
        messageService.sendChatTyping(update)
        when (command) {
            BotCommandsEnum.SILENTSTREAM -> messageService.sendSticker(messageService.randomSticker, update)
            BotCommandsEnum.FREELANCE -> return frelanceTimer(update)
            BotCommandsEnum.NOFAP -> return nofapTimer(update)
            BotCommandsEnum.FOLOPIDOR -> return foloPidor(update)
            BotCommandsEnum.FOLOPIDORTOP -> return foloPidorTop(update)
            BotCommandsEnum.FOLOUNDERDOG -> return foloUnderdog(update)
            BotCommandsEnum.FOLOPIDORALPHA -> return alphaTimer(update)
            else -> return null
        }
        return null
    }

    /**
     * Подсчет времени прошедшего с дня F
     *
     * @param update [Update]
     */
    private fun frelanceTimer(update: Update): BotApiMethod<*> {
        return messageService.buildMessage(
            """
                18 ноября 2019 года я уволился с завода по своему желанию.
                С тех пор я стремительно вхожу в IT вот уже
                *${getPeriodText(Period.between(LocalDate.of(2019, 11, 18), LocalDate.now()))}*!
            """.trimIndent(),
            update
        )
    }

    /**
     * Подсчет времени прошедшего с последнего фапа. Фо обновляет таймер
     */
    private fun nofapTimer(update: Update): BotApiMethod<*> {
        val noFapDate: LocalDate
        var noFapCount = 0
        // Фо устанавливает дату
        if (isFo(update.message.from)) {
            noFapDate = LocalDate.now()
            foloVarService.setLastFapDate(noFapDate)
        } else {
            noFapDate = foloVarService.getLastFapDate()
            noFapCount = foloVarService.getNoFapCount(update.message.chatId)
        }
        return if (noFapDate == LocalDate.now()) {
            messageService.buildMessage(
                """
                    Все эти молоденькие няшные студенточки вокруг...
                    Сорвался "Но Фап" сегодня...
                """.trimIndent(),
                update
            )
        } else {
            messageService.buildMessage(
                "Для особо озабоченных в *" +
                        RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT)
                            .format(noFapCount.toLong(), "%spellout-ordinal-masculine") +
                        "* раз повторяю тут Вам, что я с *" +
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(Locale("ru"))
                            .format(noFapDate) +
                        "* и до сих пор вот уже *" +
                        getPeriodText(
                            Period.between(
                                noFapDate,
                                LocalDate.now()
                            )
                        ) +
                        "* твёрдо и уверенно держу \"Но Фап\".",
                update
            )
        }
    }

    /**
     * Определяет фолопидора дня. Если уже определен показывает кто
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    private fun foloPidor(update: Update): BotApiMethod<*>? {
        val chatId = update.message.chatId
        if (!update.message.isUserMessage) {
            //Определяем дату и победителя предыдущего запуска
            val lastDate = foloVarService.getLastFolopidorDate(chatId)
            val lastWinner = foloVarService.getLastFolopidorWinner(chatId)

            //Определяем либо показываем победителя
            if (lastWinner == FoloVarService.INITIAL_USERID || lastDate.isBefore(LocalDate.now())) {
                //Выбираем случайного
                val foloPidor = foloPidorService.getRandom(chatId)

                //Обновляем счетчик
                foloPidor.score++
                foloPidor.lastWinDate = LocalDate.now()
                foloPidorService.save(foloPidor)

                //Обновляем текущего победителя
                foloVarService.setLastFolopidorWinner(chatId, foloPidor.id.userId)
                foloVarService.setLastFolopidorDate(chatId, LocalDate.now())

                //Поздравляем
                messageService.sendMessage(textService.setup, update)
                messageService.sendMessage(
                    textService.getPunch(
                        userService.getFoloUserNameLinked(foloPidor, chatId)
                    ), update
                )
            } else {
                return messageService.buildMessage(
                    "Фолопидор дня уже выбран, это *" +
                            userService.getFoloUserName(
                                foloPidorService.findById(chatId, lastWinner),
                                chatId
                            ) +
                            "*. Пойду лучше лампово попержу в диван",
                    update
                )
            }
        } else {
            return messageService.buildMessage(
                "Для меня вы все фолопидоры, " +
                        userService.getFoloUserName(update.message.from),
                update,
                true
            )
        }
        return null
    }

    /**
     * Показывает топ фолопидоров
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    private fun foloPidorTop(update: Update): BotApiMethod<*> {
        return if (!update.message.isUserMessage) {
            val top = StringJoiner("\n").add("Топ 10 *фолопидоров*:\n")
            val foloPidors = foloPidorService.getTop(update.message.chatId)
            for (i in foloPidors.indices) {
                val place = when (i) {
                    0 -> "\uD83E\uDD47"
                    1 -> "\uD83E\uDD48"
                    2 -> "\uD83E\uDD49"
                    else -> "\u2004*" + (i + 1) + "*.\u2004"
                }
                val foloPidor = foloPidors[i]
                top.add(
                    place + userService.getFoloUserName(foloPidor, update.message.chatId) + " — _" +
                            getNumText(foloPidor.score, NumTypeEnum.COUNT) + "_"
                )
            }
            messageService.buildMessage(top.toString(), update)
        } else {
            messageService.buildMessage("Андрей - почетный фолопидор на все времена!", update)
        }
    }

    private fun foloUnderdog(update: Update): BotApiMethod<*> {
        return if (!update.message.isUserMessage) {
            val foloUnderdogs = foloPidorService.getUnderdog(update.message.chatId)
            if (foloUnderdogs.isNotEmpty()) {
                messageService.buildMessage(
                    text = "Когда-нибудь и вы станете *фолопидорами дня*, уважаемые фанаты " +
                            "и милые фанаточки, просто берите пример с Андрея!\n\n" +
                            foloUnderdogs.joinToString(
                                separator = "\n• ",
                                prefix = "• ",
                                transform = { foloPidor ->
                                    userService.getFoloUserName(foloPidor, update.message.chatId)
                                }
                            ),
                    update = update
                )
            } else {
                return messageService.buildMessage(
                    text = "Все *фолопидоры* хотя бы раз побывали *фолопидорами дня*, это потрясающе!",
                    update = update
                )
            }
        } else {
            messageService.buildMessage(
                "Для меня вы все фолопидоры, " +
                        userService.getFoloUserName(update.message.from),
                update,
                true
            )
        }
    }

    /**
     * Подсчет времени до дня рождения альфы
     *
     * @param update [Update]
     */
    private fun alphaTimer(update: Update): BotApiMethod<*> {
        val alfaBirthday = LocalDate.of(1983, 8, 9)
        val alphaBirthdayThisYear = alfaBirthday.withYear(LocalDate.now().year)
        val nextAlphaBirthday =
            if (alphaBirthdayThisYear.isBefore(LocalDate.now()))
                alphaBirthdayThisYear.plusYears(1)
            else alphaBirthdayThisYear

        return if (nextAlphaBirthday == LocalDate.now()) {
            messageService.buildMessage(
                "Поздравляю моего хорошего друга и главного фолопидора " +
                        "[Андрея](tg://user?id=$ANDREW_ID) с днем рождения!\nСегодня ему исполнилось " +
                        "*${
                            getNumText(
                                Period.between(alfaBirthday, nextAlphaBirthday).years,
                                NumTypeEnum.YEARISH
                            )
                        }*!",
                update
            )
        } else {
            messageService.buildMessage(
                "День рождения моего хорошего друга и главного фолопидора " +
                        "[Андрея](tg://user?id=$ANDREW_ID) *" +
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            .withLocale(Locale("ru"))
                            .format(alfaBirthday) +
                        "* через *${getPeriodText(Period.between(LocalDate.now(), nextAlphaBirthday))}*",
                update
            )
        }
    }
}