package ru.iscoach.service

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.iscoach.service.model.Product
import ru.iscoach.service.model.ProductCategory
import ru.iscoach.service.model.toCallbackCommand

@Component
class KeyboardBuilder() : KLogging() {

    fun buildInlineButton(product: Product, pay: Boolean = false): InlineKeyboardButton =
        InlineKeyboardButton.builder()
            .text(product.label)
            .callbackData(product.toCallbackCommand())
            .pay(pay)
            .build()

    fun buildProductCategoryKeyboard(productCategory: ProductCategory): InlineKeyboardMarkup =
        InlineKeyboardMarkup
            .builder()
            .also { keyboardBuilder ->
                Product.values()
                    .filter { it.category == productCategory }
                    .forEach {
                        keyboardBuilder
                            .keyboardRow(
                                buildInlineButton(it, true).toInlineButtonRow()
                            )
                    }
            }.build()


    fun InlineKeyboardButton.toInlineButtonRow(): List<InlineKeyboardButton> = listOf(this)
}

