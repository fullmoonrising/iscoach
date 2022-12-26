package com.telegram.folobot.config

import com.telegram.folobot.service.ScheduleService
import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom

@Configuration
@EnableScheduling
class DynamicSchedulingConfig(
    private val scheduleService: ScheduleService
) : SchedulingConfigurer, KLogging() {
    @Bean
    fun taskExecutor(): Executor {
        return Executors.newSingleThreadScheduledExecutor()
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor())
        taskRegistrar.addTriggerTask(
            { scheduleService.whatAboutIT() },
            {
                val lastCompletionTime = it.lastCompletionTime()?.toInstant()
                    .also {
                        logger.info {
                            "Last task completion time is ${
                                it?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) } ?: "undefined"
                            }"
                        }
                    }
                generateNextExecutionTime(lastCompletionTime)
                    .also {
                        logger.info {
                            "Next task execution time is ${LocalDateTime.ofInstant(it, ZoneId.systemDefault())}"
                        }
                    }
            }
        )
    }

    private fun generateNextExecutionTime(lastCompletionTime: Instant?): Instant {
        val todayBegin = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0)
        val todayEnd = LocalDateTime.now().withHour(23).withMinute(0).withSecond(0)
        val alreadyTriggeredToday =
            lastCompletionTime?.let { LocalDate.ofInstant(it, ZoneId.systemDefault()) } == LocalDate.now()
        return if (alreadyTriggeredToday || LocalDateTime.now() > todayEnd) {
            Instant.ofEpochSecond(
                ThreadLocalRandom
                    .current()
                    .nextLong(
                        todayBegin.plusDays(1).atZone(ZoneId.systemDefault()).toEpochSecond(),
                        todayEnd.plusDays(1).atZone(ZoneId.systemDefault()).toEpochSecond(),
                    )
            )
        } else {
            Instant.ofEpochSecond(
                ThreadLocalRandom
                    .current()
                    .nextLong(
                        maxOf(todayBegin, LocalDateTime.now().plusMinutes(1))
                            .atZone(ZoneId.systemDefault()).toEpochSecond(),
                        todayEnd.atZone(ZoneId.systemDefault()).toEpochSecond(),
                    )
            )
        }
    }

}