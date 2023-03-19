package ru.iscoach.event

import mu.KLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.iscoach.service.handler.Handler


@Service
class UpdateReceivedListener(
    private val handlers: List<Handler>
) : KLogging() {
    @EventListener
    fun handleUpdateReceived(updateReceivedEvent: UpdateReceivedEvent) {
        val update = updateReceivedEvent.update
        handlers.firstOrNull { it.canHandle(update) }?.handle(update)
    }
}