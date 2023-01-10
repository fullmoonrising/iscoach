package com.telegram.folobot.service

import com.telegram.folobot.IdUtils
import com.telegram.folobot.Utils
import com.telegram.folobot.model.NumTypeEnum
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

@Service
class TaskService(
    private val messageService: MessageService,
    private val foloPidorService: FoloPidorService,
    private val userService: UserService,
    private val foloIndexService: FoloIndexService
) : KLogging() {
    fun foloAnimal(chatId: Long) {
        messageService.sendVoice(chatId = chatId, voiceId = messageService.randomVoice)
    }

    fun dayStats(chatId: Long) {
        messageService.sendMessage(
            foloPidorService.getTopActive(chatId).withIndex().joinToString(
                separator = "\n",
                prefix = "*Самые активные фолопидоры сегодня*:\n\n",
                transform = {
                    "\u2004*${it.index + 1}*.\u2004${
                        userService.getFoloUserName(it.value, chatId)
                    } — ${Utils.getNumText(it.value.messagesPerDay, NumTypeEnum.MESSAGE)}"
                }
            ),
            chatId
        ).also { logger.info { "Sent day stats to ${IdUtils.getChatIdentity(chatId)}" } }
    }

    fun foloIndex(chatId: Long) {
        val photoPath: String
        val indexText: String

        val todayIndex = (foloIndexService.calcAndSaveIndex(chatId, LocalDate.now()) * 100)
            .roundToInt().toDouble() / 100
        val yesterdayIndex = ((foloIndexService.getById(chatId, LocalDate.now().minusDays(1)).index ?: 0.0) * 100)
            .roundToInt().toDouble() / 100
        val indexChange = ((todayIndex - yesterdayIndex) * 100).roundToInt()

        if (indexChange > 0) {
            photoPath = "/static/images/index/index_up.jpg"
            indexText = "растет на ${Utils.getNumText(indexChange.absoluteValue, NumTypeEnum.POINT)}"
        } else if (indexChange < 0) {
            photoPath = "/static/images/index/index_down.jpg"
            indexText = "падает на ${Utils.getNumText(indexChange.absoluteValue, NumTypeEnum.POINT)}"
        } else {
            photoPath = "/static/images/index/index_neutral.jpg"
            indexText = "не изменился"
        }
        val forecast = listOf("Продавать", "Держать", "Покупать").random()

        messageService.sendPhotoFromResources(
            photoPath,
            "Индекс фолоактивности *$indexText* и на сегодня составляет *$todayIndex%* от среднегодового значения\n" +
                    "Консенсус-прогноз: *$forecast* _(Основано на мнении ${Random.Default.nextInt(2,5)} аналитиков)_\n" +
                    "#фолоиндекс",
            chatId
        ).also { logger.info { "Sent foloindex to ${IdUtils.getChatIdentity(chatId)}" } }
    }
}