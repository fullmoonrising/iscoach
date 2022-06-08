package com.telegram.folobot.service

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

@Component
class TextService {
    val foloPidorPunch = getText("foloPidorPunch")
    val foloPidorSetup = getText("foloPidorSetup")
    val quotesforAndrew = getText("quotesForAndrew")

    /**
     * Чтение текстов из ресурсов
     * @param textName Имя файла без расширения
     * @return [<] Тексты
     */
    final fun getText(textName: String): List<String> {
        try {
            BufferedReader(
                InputStreamReader(
                    ClassPathResource("texts\\$textName.txt")
                        .inputStream
                )
            ).use { reader ->
                return reader.lines()
                    .map { line: String -> line.replace("\\n", System.lineSeparator()) }
                    .toList()
            }
        } catch (e: IOException) {
            return ArrayList()
        }
    }

    val setup: String
        get() = foloPidorSetup[SplittableRandom().nextInt(foloPidorSetup.size)]

    fun getPunch(userName: String?): String {
        return String.format(foloPidorPunch[SplittableRandom().nextInt(foloPidorPunch.size)], userName)
    }

    val quoteforAndrew: String
        get() = quotesforAndrew[SplittableRandom().nextInt(quotesforAndrew.size)]
}