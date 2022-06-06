package com.telegram.folobot;

import com.telegram.folobot.constants.NumTypeEnum;

import java.text.ChoiceFormat;
import java.time.Period;
import java.util.Map;

import static java.util.Map.entry;

public class Utils {
    static Map<NumTypeEnum, String[]> numText = Map.ofEntries(
            entry(NumTypeEnum.YEAR, new String[]{"лет", "год", "года", "лет"}),
            entry(NumTypeEnum.MONTH, new String[]{"месяцев", "месяц", "месяца", "месяцев"}),
            entry(NumTypeEnum.DAY, new String[]{"дней", "день", "дня", "дней"}),
            entry(NumTypeEnum.COUNT, new String[]{"раз", "раз", "раза", "раз"}));

    /**
     * Текстовое представление части даты (1901 -> 1901 год)
     *
     * @param part часть даты
     * @param numTypeEnum {@link NumTypeEnum}
     * @return Текст
     */
    public static String getNumText(int part, NumTypeEnum numTypeEnum) {
        ChoiceFormat format = new ChoiceFormat(new double[]{0, 1, 2, 5}, numText.get(numTypeEnum));
        int rule = 11 <= (part % 100) && (part % 100) <= 14 ? part : part % 10;
        return String.valueOf(part) + ' ' + format.format(rule);
    }

    public static String getPeriodText(Period period) {
        StringBuilder stringBuilder = new StringBuilder();
        if (period.getYears() > 0) {
            stringBuilder.append(getNumText(Math.abs(period.getYears()), NumTypeEnum.YEAR));
        }
        if (period.getMonths() > 0) {
            if (!stringBuilder.isEmpty()) { stringBuilder.append(", "); }
            stringBuilder.append(getNumText(Math.abs(period.getMonths()), NumTypeEnum.MONTH));
        }
        if (period.getDays() > 0) {
            if (!stringBuilder.isEmpty()) { stringBuilder.append(" и "); }
            stringBuilder.append(getNumText(Math.abs(period.getDays()), NumTypeEnum.DAY));
        }
        return stringBuilder.toString();
    }

    public static void printExeptionMessage(Throwable throwable) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        System.out.println(stackTrace[stackTrace.length - 1] + " > " + throwable.getMessage());
    }

}
