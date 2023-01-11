package ru.iscoach.config

import jakarta.annotation.PostConstruct
import mu.KLogging
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Configuration
class TimeZoneConfig() : KLogging() {
    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))
        logger.info { "App started ${LocalDate.now()} at ${LocalTime.now().withNano(0)} MSK" }
        logger.info { "Hello, coach!" }
    }
}