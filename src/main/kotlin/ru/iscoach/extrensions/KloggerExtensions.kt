package ru.iscoach.extrensions

import mu.KLogger
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import ru.iscoach.service.model.Action
import ru.iscoach.service.model.BotCommand
import ru.iscoach.service.model.Product

fun KLogger.addActionReceived(action: Action, chat: Chat) =
    this.info { "Received request with action $action in chat ${chat.getName()}" }
fun KLogger.addPreCheckoutQueryReceived(user: User) =
    this.info { "Received preCheckout query from ${user.getName()}" }

fun KLogger.addOutdatedInvoiceCheckout(user: User) =
    this.info { "Attempt to use an outdated invoice by ${user.getName()}" }

fun KLogger.addSuccessfulPaymentReceived(user: User, chat: Chat) =
    this.info { "Received successful payment from ${user.getName()} in chat ${chat.getName()}" }
fun KLogger.addCallbackCommandReceived(product: Product, user: User) =
    this.info { "Received callback command with product $product from ${user.getName()}" }

fun KLogger.addCommandReceived(botCommand: BotCommand?, user: User, chat: Chat) =
    this.info { "Received command ${botCommand ?: "UNDEFINED"} from ${user.getName()} in chat ${chat.getName()}" }