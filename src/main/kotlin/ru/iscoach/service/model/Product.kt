package ru.iscoach.service.model

enum class Product(val label: String, val category: ProductCategory) {
    CONSULTATION("Психологическое консультирование", ProductCategory.SERVICE),
    SESSION("Коучинговая сессия", ProductCategory.SERVICE),
    REGRESSION("Регрессия", ProductCategory.SERVICE),
    PACKAGE("Пакет сессий", ProductCategory.SERVICE),
    MEDITATION01("Медитация интуиции", ProductCategory.DIGITAL_GOODS),
    MEDITATION02("Медитация самоценности", ProductCategory.DIGITAL_GOODS),
    MEDITATION03("Настройка на лучший день", ProductCategory.DIGITAL_GOODS);

    companion object {
        private val commands = Product.values().associateBy { "/$it" }
        fun fromCommand(command: String?) = commands[command]
        fun isMyCommand(command: String?) = commands.contains(command)
    }
}

enum class ProductCategory(val label: String) {
    SERVICE("Услуга"),
    DIGITAL_GOODS("Цифровой товар"),
}

fun Product.toCallbackCommand(): String = "/$this"