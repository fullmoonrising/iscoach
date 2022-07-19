package com.telegram.folobot.config

import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalTime
import java.util.TimeZone
import javax.annotation.PostConstruct

@Configuration
class Configuration {
    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))
        println("Bot started ${LocalDate.now()} at ${LocalTime.now().withNano(0)} MSK")
    }
}