package com.telegram.folobot;

import com.telegram.folobot.constants.NumTypeEnum;

import java.text.ChoiceFormat;
import java.time.Period;
import java.util.StringJoiner;

public class Utils {
    /**
     * Текстовое представление части даты (1901 -> 1901 год)
     *
     * @param part     часть даты
     * @param numTypeEnum {@link NumTypeEnum}
     * @return Текст
     */
    public static String getNumText(int part, NumTypeEnum numTypeEnum) {
        //TODO перенести в MAP
        double[] limits = {0, 1, 2, 5};
        String[] strings = switch (numTypeEnum) {
            case YEAR -> new String[]{"лет", "год", "года", "лет"};
            case MONTH -> new String[]{"месяцев", "месяц", "месяца", "месяцев"};
            case DAY -> new String[]{"дней", "день", "дня", "дней"};
            case COUNT -> new String[]{"раз", "раз", "раза", "раз"};
        };

        ChoiceFormat format = new ChoiceFormat(limits, strings);
        int rule = 11 <= (part % 100) && (part % 100) <= 14 ? part : part % 10;
        return String.valueOf(part) + ' ' + format.format(rule);
    }

    //TODO java format date by template
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
