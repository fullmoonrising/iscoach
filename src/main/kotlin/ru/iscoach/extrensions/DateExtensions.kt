package ru.iscoach.extrensions

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

fun LocalDate.toOffsetAtStartOfDay(): OffsetDateTime =
    this.atStartOfDay(ZoneId.of("Europe/Moscow")).toOffsetDateTime()

fun LocalDate.toOffsetAtEndOfDay(): OffsetDateTime =
    this.plusDays(1).atStartOfDay(ZoneId.of("Europe/Moscow")).toOffsetDateTime().minusNanos(1)