package com.telegram.folobot;

import com.telegram.folobot.enums.NumType;

import java.text.ChoiceFormat;

public class Utils {
    /**
     * Текстовое представление части даты (1901 -> 1901 год)
     *
     * @param part     часть даты
     * @param numType {@link NumType}
     * @return Текст
     */
    public static String getNumText(int part, NumType numType) {
        double[] limits = {0, 1, 2, 5};
        String[] strings;
        switch (numType) {
            case YEAR:
                strings = new String[]{"лет", "год", "года", "лет"};
                break;
            case MONTH:
                strings = new String[]{"месяцев", "месяц", "месяца", "месяцев"};
                break;
            case DAY:
                strings = new String[]{"дней", "день", "дня", "дней"};
                break;
            case COUNT:
                strings = new String[]{"раз", "раз", "раза", "раз"};
                break;
            default:
                strings = new String[]{};
        }

        ChoiceFormat format = new ChoiceFormat(limits, strings);
        int rule = 11 <= (part % 100) && (part % 100) <= 14 ? part : part % 10;
        return String.valueOf(part) + ' ' + format.format(rule);
    }

}
