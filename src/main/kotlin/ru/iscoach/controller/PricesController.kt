package ru.iscoach.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.PriceListRepo
import ru.iscoach.service.DonateSessionService
import ru.iscoach.service.model.Product
import ru.iscoach.service.model.ProductCategory

@Controller
class PricesController(
    private val priceListRepo: PriceListRepo,
    private val donateSessionService: DonateSessionService,
) : KLogging() {
    @GetMapping("/prices")
    fun prices(model: MutableMap<String, Any>): String {
        model["products"] =
            priceListRepo.findAllByOrderByOrder()
                .filter { it.id.category == ProductCategory.SERVICE }
                .let { priceList ->
                    if (donateSessionService.checkIfBookedToday()) {
                        priceList.filterNot { it.id == Product.DONATE_SESSION }
                    } else priceList
                }
                .map { it.toDto() }
        return "prices"
    }
}