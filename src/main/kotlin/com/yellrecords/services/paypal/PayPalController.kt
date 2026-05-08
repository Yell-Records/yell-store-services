package com.yellrecords.services.paypal

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import tools.jackson.databind.JsonNode

@RestController
@RequestMapping("/api/paypal")
class PayPalController(
    private val payPalClient: PayPalClient,
) {
    @PostMapping("/create-order")
    fun createOrder(): Mono<JsonNode> {
        val body =
            mapOf(
                "intent" to "CAPTURE",
                "purchase_units" to
                    listOf(mapOf("amount" to mapOf("currency_code" to "USD", "value" to "10.00"))),
            )

        return payPalClient.post("/v2/checkout/orders", body)
    }

    @PostMapping("/capture-order/{orderId}")
    fun captureOrder(
        @PathVariable orderId: String,
    ): Mono<JsonNode> = payPalClient.post("/v2/checkout/orders/{orderId}/capture", emptyMap<String, Any>())
}
