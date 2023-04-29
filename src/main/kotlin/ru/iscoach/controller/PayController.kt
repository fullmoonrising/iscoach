package ru.iscoach.controller

import jakarta.ws.rs.BadRequestException
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException.BadRequest
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.PriceListRepo
import ru.iscoach.service.model.Product

@Controller
class PayController(
    private val priceListRepo: PriceListRepo
) : KLogging() {
    @GetMapping("/pay")
    fun pay(
        model: MutableMap<String, Any>,
        @RequestParam(value = "product-id", defaultValue = "SESSION") product: Product
    ): String {
        model["product"] = priceListRepo.findItemById(product)?.toDto()
            ?: throw RuntimeException("Product $product not found in price list")
        return "pay"
    }
}