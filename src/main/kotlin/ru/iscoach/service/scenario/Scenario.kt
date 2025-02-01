package ru.iscoach.service.scenario

import org.telegram.telegrambots.meta.api.objects.Update
import ru.iscoach.extrensions.from

class Scenario(firstStep: ScenarioStep) {
    private var currentStep: ScenarioStep? = firstStep

    fun doNextRequest(update: Update): Boolean {
        currentStep?.let { step ->
            if (step.validateRequirements(update)) {
                step.sendRequest(update.from.id)
                currentStep = step.nextStep
            } else {
                step.sendRequirementsNotMetMessage(update.from.id)
            }
        }
        return currentStep == null
    }
}