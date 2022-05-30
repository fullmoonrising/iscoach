package com.telegram.folobot.service;

import com.telegram.folobot.constants.VarTypeEnum;
import com.telegram.folobot.domain.FoloVar;
import com.telegram.folobot.domain.FoloVarId;
import com.telegram.folobot.repos.FoloVarRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FoloVarService {
    private final Long COMMON_CHATID = 0L;
    private final FoloVarRepo foloVarRepo;

    /**
     * Дата последнего определения фолопидора
     *
     * @param chatId ID чата
     * @return {@link LocalDate}
     */
    public LocalDate getLastFolopidorDate(Long chatId) {
        return foloVarRepo.findById(new FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_DATE.name()))
                .map(foloVar -> LocalDate.parse(foloVar.getValue()))
                .orElse(LocalDate.of(1900, 1, 1));
    }

    /**
     * Сохранить дату последнего определения фолопидора
     *
     * @param chatId ID чата
     * @param value {@link LocalDate} Дата
     */
    public void setLastFolopidorDate(Long chatId, LocalDate value) {
        foloVarRepo.save(new FoloVar(
                new FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_DATE.name()), value.toString()));
    }

    /**
     * Последний фолопидор
     *
     * @param chatId ID чата
     * @return {@link Long} userid
     */
    public Long getLastFolopidorWinner(Long chatId) {
        return foloVarRepo.findById(new FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_USERID.name()))
                .map(foloVar -> Long.parseLong(foloVar.getValue()))
                .orElse(null);
    }

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatId ID чата
     * @param value  {@link Long} userid
     */
    public void setLastFolopidorWinner(Long chatId, Long value) {
        foloVarRepo.save(new FoloVar(
                new FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_USERID.name()), Long.toString(value)));
    }

    /**
     * Дата последнего фапа
     * @return {@link LocalDate}
     */
    public LocalDate getLastFapDate() {
        return foloVarRepo.findById(new FoloVarId(COMMON_CHATID, VarTypeEnum.LAST_FAP_DATE.name()))
                .map(foloVar -> LocalDate.parse(foloVar.getValue()))
                .orElse(LocalDate.of(2020, 1, 1));
    }

    /**
     * Сохранить дату последнего фапа
     *
     * @param value {@link LocalDate} Дата
     */
    public void setLastFapDate(LocalDate value) {
        foloVarRepo.save(new FoloVar(
                new FoloVarId(COMMON_CHATID, VarTypeEnum.LAST_FAP_DATE.name()), value.toString()));
    }

    /**
     * Последний фолопидор
     *
     * @param chatId ID чата
     * @return {@link Integer} Счетчик
     */
    public Integer getNoFapCount(Long chatId) {
        Integer noFapCount = foloVarRepo.findById(new FoloVarId(chatId, VarTypeEnum.LAST_FAP_COUNT.name()))
                .map(foloVar -> Integer.parseInt(foloVar.getValue())).orElse(0) + 1;
        setNoFapCount(chatId, noFapCount);
        return noFapCount;
    }

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatId ID чата
     * @param value  {@link Integer} Счетчик
     */
    public void setNoFapCount(Long chatId, Integer value) {
        foloVarRepo.save(new FoloVar(
                new FoloVarId(chatId, VarTypeEnum.LAST_FAP_COUNT.name()), Integer.toString(value)));
    }

}
