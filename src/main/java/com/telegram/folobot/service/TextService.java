package com.telegram.folobot.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

@Component
public class TextService {
    public final List<String> foloPidorPunch = getText("foloPidorPunch");
    public final List<String> foloPidorSetup = getText("foloPidorSetup");
    public final List<String> quotesforAndrew = getText("quotesForAndrew");

    /**
     * Чтение текстов из ресурсов
     * @param textName Имя файла без расширения
     * @return {@link List<String>} Тексты
     */
    public List<String> getText(String textName) {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("texts\\" + textName + ".txt")
                                .getInputStream()))) {
            return reader.lines()
                    .map(line -> line.replace("\\n", System.lineSeparator()))
                    .toList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public String getSetup() {
        return foloPidorSetup.get(new SplittableRandom().nextInt(foloPidorSetup.size()));
    }

    public String getPunch(String userName) {
        return String.format(foloPidorPunch.get(new SplittableRandom().nextInt(foloPidorPunch.size())), userName);
    }

    public String getQuoteforAndrew() {
        return quotesforAndrew.get(new SplittableRandom().nextInt(quotesforAndrew.size()));
    }

}
