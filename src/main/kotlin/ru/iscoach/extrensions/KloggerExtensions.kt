package ru.iscoach.extrensions

import mu.KLogger
import ru.iscoach.service.model.Actions

fun KLogger.addActionReceived(action: Actions) = this.info { "Received request with action $action" }
fun KLogger.addPreCheckoutQueryReceived() = this.info { "Received preCheckout query" }
fun KLogger.addSuccessfulPaymentReceived() = this.info { "Received successful payment" }
fun KLogger.addInlineQueryReceived() = this.info { "Received inline query" }
