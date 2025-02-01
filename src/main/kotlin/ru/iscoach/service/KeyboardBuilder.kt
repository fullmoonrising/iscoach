package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.iscoach.service.model.Product
import ru.iscoach.service.model.ProductCategory
import ru.iscoach.service.model.toCallbackCommand
import ru.iscoach.service.model.type.BotCommand

@Component
class KeyboardBuilder() : KLogging() {
    fun buildProductCategoryKeyboard(productCategory: ProductCategory): InlineKeyboardMarkup =
        InlineKeyboardMarkup
            .builder()
            .also { keyboardBuilder ->
                Product.entries
                    .filter { it.category == productCategory }
                    .forEach {
                        keyboardBuilder
                            .keyboardRow(
                                buildInlineButton(it, true).toInlineButtonRow()
                            )
                    }
            }.build()

    fun buildMainMenu(): ReplyKeyboardMarkup =
        ReplyKeyboardMarkup
            .builder()
            .isPersistent(true)
            .keyboardRow(
                listOf(
                    buildCommandButton(BotCommand.SESSION),
                    buildCommandButton(BotCommand.MEDITATION)
                )
                    .toRow()
            )
            .resizeKeyboard(true)
            .build()

//    fun buildMainMenu(): ForceReplyKeyboard =
//        ForceReplyKeyboard
//            .builder()
//            .forceReply(true)
//            .build()

    private fun buildInlineButton(product: Product, pay: Boolean = false): InlineKeyboardButton =
        InlineKeyboardButton.builder()
            .text(product.label)
            .callbackData(product.toCallbackCommand())
            .pay(pay)
            .build()

    private fun buildCommandButton(command: BotCommand): KeyboardButton =
        KeyboardButton
            .builder()
            .text(command.command)
            .build()

    private fun InlineKeyboardButton.toInlineButtonRow(): List<InlineKeyboardButton> = listOf(this)

    private fun List<KeyboardButton>.toRow() = KeyboardRow(this)
}

