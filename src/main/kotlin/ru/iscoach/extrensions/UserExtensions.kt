package ru.iscoach.extrensions

import org.telegram.telegrambots.meta.api.objects.User

fun User.getName() = "${ this.firstName }${ this.lastName?.let { " $it" } ?: this.userName ?: this.id }"