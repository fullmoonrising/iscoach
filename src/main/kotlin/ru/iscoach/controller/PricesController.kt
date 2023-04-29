package ru.iscoach.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.PriceListRepo

@Controller
class PricesController(
    private val priceListRepo: PriceListRepo
) : KLogging() {
    @GetMapping("/prices")
    fun prices(model: MutableMap<String, Any>): String {
        model["products"] = priceListRepo.findAllByOrderByOrder().map { it.toDto() }
        return "prices"
    }
}