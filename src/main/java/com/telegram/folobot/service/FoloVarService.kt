package com.telegram.folobot.service

import com.telegram.folobot.constants.VarTypeEnum
import com.telegram.folobot.domain.FoloVarEntity
import com.telegram.folobot.domain.FoloVarId
import com.telegram.folobot.repos.FoloVarRepo
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class FoloVarService(private val foloVarRepo: FoloVarRepo) {
    private val COMMON_CHATID = 0L

    /**
     * Дата последнего определения фолопидора
     *
     * @param chatId ID чата
     * @return [LocalDate]
     */
    fun getLastFolopidorDate(chatId: Long): LocalDate {
        return foloVarRepo.findById(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_DATE.name))
            .map { foloVarEntity -> LocalDate.parse(foloVarEntity.value) }
            .orElse(LocalDate.of(2020, 1, 1))
    }

    /**
     * Сохранить дату последнего определения фолопидора
     *
     * @param chatId ID чата
     * @param value [LocalDate] Дата
     */
    fun setLastFolopidorDate(chatId: Long, value: LocalDate) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_DATE.name), value.toString()))
    }

    /**
     * Последний фолопидор
     *
     * @param chatId ID чата
     * @return [Long] userid
     */
    fun getLastFolopidorWinner(chatId: Long): Long {
        return foloVarRepo.findById(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_USERID.name))
            .map { foloVarEntity -> foloVarEntity.value.toLong() }
            .orElse(0L) //TODO поменять на null
    }

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatId ID чата
     * @param userId  [Long] Id пользователя
     */
    fun setLastFolopidorWinner(chatId: Long, userId: Long) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(chatId, VarTypeEnum.LAST_FOLOPIDOR_USERID.name), userId.toString()))
    }
    /**
     * Дата последнего фапа
     * @return [LocalDate]
     */
    fun getLastFapDate(): LocalDate {
        return foloVarRepo.findById(FoloVarId(COMMON_CHATID, VarTypeEnum.LAST_FAP_DATE.name))
            .map { foloVarEntity -> LocalDate.parse(foloVarEntity.value) }
            .orElse(LocalDate.of(2020, 1, 1))
    }

    /**
     * Сохранить дату последнего фапа
     *
     * @param fapDate [LocalDate] Дата
     */
    fun setLastFapDate(fapDate: LocalDate) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(COMMON_CHATID, VarTypeEnum.LAST_FAP_DATE.name), fapDate.toString()))
    }

    /**
     * Счетчик фап запросов
     *
     * @param chatId ID чата
     * @return [Integer] Счетчик
     */
    fun getNoFapCount(chatId: Long): Int {
        val noFapCount: Int = foloVarRepo.findById(FoloVarId(chatId, VarTypeEnum.LAST_FAP_COUNT.name))
            .map{ foloVarEntity -> foloVarEntity.value.toInt() }
            .orElse(0) + 1
        setNoFapCount(chatId, noFapCount)
        return noFapCount
    }

    /**
     * Сохранить счетчик фап запросов
     *
     * @param chatId ID чата
     * @param value  [Integer] Счетчик
     */
    fun setNoFapCount(chatId: Long, value: Int) {
        foloVarRepo.save(FoloVarEntity(FoloVarId(chatId, VarTypeEnum.LAST_FAP_COUNT.name), Integer.toString(value)))
    }
}