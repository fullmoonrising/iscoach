package ru.iscoach.extrensions

import com.fasterxml.jackson.module.kotlin.readValue
import ru.iscoach.config.objectMapper


fun <T> T.toJson(): String = objectMapper.writeValueAsString(this)
inline fun <reified T> String.toObject() = objectMapper.readValue<T>(this)