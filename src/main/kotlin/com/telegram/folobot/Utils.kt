package com.telegram.folobot

import com.telegram.folobot.constants.NumTypeEnum
import java.text.ChoiceFormat
import java.time.Period
import java.util.Map

object Utils {
    var numText = Map.ofEntries(
        Map.entry(NumTypeEnum.YEAR, arrayOf("лет", "год", "года", "лет")),
        Map.entry(NumTypeEnum.MONTH, arrayOf("месяцев", "месяц", "месяца", "месяцев")),
        Map.entry(NumTypeEnum.DAY, arrayOf("дней", "день", "дня", "дней")),
        Map.entry(NumTypeEnum.COUNT, arrayOf("раз", "раз", "раза", "раз"))
    )

    /**
     * Текстовое представление части даты (1901 -> 1901 год)
     *
     * @param part часть даты
     * @param numTypeEnum [NumTypeEnum]
     * @return Текст
     */
    @JvmStatic
    fun getNumText(part: Int, numTypeEnum: NumTypeEnum): String {
        val format = ChoiceFormat(doubleArrayOf(0.0, 1.0, 2.0, 5.0), numText[numTypeEnum])
        val rule = if (11 <= part % 100 && part % 100 <= 14) part else part % 10
        return part.toString() + ' ' + format.format(rule.toLong())
    }

    @JvmStatic
    fun getPeriodText(period: Period): String {
        val stringBuilder = StringBuilder()
        if (period.years > 0) {
            stringBuilder.append(getNumText(Math.abs(period.years), NumTypeEnum.YEAR))
        }
        if (period.months > 0) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(", ")
            }
            stringBuilder.append(getNumText(Math.abs(period.months), NumTypeEnum.MONTH))
        }
        if (period.days > 0) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(" и ")
            }
            stringBuilder.append(getNumText(Math.abs(period.days), NumTypeEnum.DAY))
        }
        return stringBuilder.toString()
    }

    @JvmStatic
    fun printExeptionMessage(throwable: Throwable) {
        val stackTrace = throwable.stackTrace
        println(stackTrace[stackTrace.size - 1].toString() + " > " + throwable.message)
    }
}