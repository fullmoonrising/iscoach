package ru.iscoach.extrensions

import org.telegram.telegrambots.meta.api.objects.Chat

fun Chat.getName(): String = this.title ?: this.id.toString()