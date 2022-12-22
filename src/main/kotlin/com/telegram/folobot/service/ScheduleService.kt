package com.telegram.folobot.service

import com.telegram.folobot.ChatId.Companion.FOLOCHAT_ID
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleService(
    private val taskService: TaskService,


) {
    fun whatAboutIT() {
        taskService.whatAboutIT(FOLOCHAT_ID)
    }

    @Scheduled(cron = "0 59 23 ? * MON-FRI")
    private fun dayStats() {
        taskService.dayStats(FOLOCHAT_ID)
    }
}