package ru.iscoach.service.model

import com.fasterxml.jackson.annotation.JsonProperty
import ru.iscoach.extrensions.format
import ru.iscoach.service.model.entity.PriceListItem
import java.io.Serializable

data class InvoiceProviderData(
    val receipt: Receipt,
    val customer: Customer?
) : Serializable {
    data class Receipt(
        val items: List<ReceiptItem>,
    ) : Serializable {
        data class ReceiptItem(
            val description: String,
            val quantity: String = 1.toString(),
            val amount: Amount,
            @JsonProperty("vat_code")
            val vatCode: Int = 1
        ) : Serializable {
            data class Amount(
                val value: String,
                val currency: String = "RUB"
            ) : Serializable
        }
    }

    data class Customer(
        val email: String,
        val phone: String
    ): Serializable

    constructor(product: PriceListItem, customer: Customer? = null) : this(
        receipt = Receipt(
            listOf(
                Receipt.ReceiptItem(
                    description = product.id.label,
                    amount = Receipt.ReceiptItem.Amount(
                        value = (product.amount / 100.0).format()
                    ),
                )
            )
        ),
        customer = customer
    )
}
