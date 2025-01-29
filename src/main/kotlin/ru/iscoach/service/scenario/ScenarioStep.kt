package ru.iscoach.service.scenario

import org.telegram.telegrambots.meta.api.objects.Update

interface ScenarioStep {
    val nextStep: ScenarioStep?
    fun validateRequirements(update: Update): Boolean
    fun sendRequirementsNotMetMessage(userId: Long)
    fun sendRequest(userId: Long)
}