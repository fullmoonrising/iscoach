package ru.iscoach.extrensions

fun String.telegramEscape() =
    this.trimMargin().trimIndent()
        .replace("""[_*\[\]()~>#+=\-|{}.!]""".toRegex()) { """\${it.value}""" }