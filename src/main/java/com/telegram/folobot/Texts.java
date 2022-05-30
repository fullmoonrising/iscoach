package com.telegram.folobot;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class Texts {
    public static final List<String> foloPidorPunch = getText("foloPidorPunch");
    public static final List<String> foloPidorSetup = getText("foloPidorSetup");
    public static final List<String> quotesforAndrew = getText("quotesforAndrew");

    /**
     * Чтение текстов из ресурсов
     * @param textName Имя файла без расширения
     * @return {@link List<String>} Тексты
     */
    public static List<String> getText(String textName) {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("texts\\" + textName + ".txt")
                                .getInputStream()))) {
            return reader.lines().toList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static String getSetup() {
        return foloPidorSetup.get(new SplittableRandom().nextInt(foloPidorSetup.size()));
    }

    public static String getPunch(String userName) {
        return String.format(foloPidorPunch.get(new SplittableRandom().nextInt(foloPidorPunch.size())), userName);
    }

    public static String getQuoteforAndrew() {
        return quotesforAndrew.get(new SplittableRandom().nextInt(quotesforAndrew.size()));
    }

}
