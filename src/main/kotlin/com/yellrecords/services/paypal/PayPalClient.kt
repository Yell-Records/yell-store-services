package com.yellrecords.services.paypal

import com.yellrecords.services.config.PayPalProperties
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.treeToValue

@Service
class PayPalClient(
    private val paypalProps: PayPalProperties,
    private val objectMapper: ObjectMapper,
) {
    private val baseUrl =
        when (paypalProps.mode) {
            PayPalMode.LIVE -> "https://api-m.paypal.com"
            PayPalMode.SANDBOX -> "https://api-m.sandbox.paypal.com"
        }

    private val client = WebClient.builder().baseUrl(baseUrl).build()

    private fun getAccessToken(): Mono<String> =
        client
            .post()
            .uri("$baseUrl/v1/oauth2/token")
            .headers {
                it.setBasicAuth(paypalProps.clientId, paypalProps.clientSecret)
                it.contentType = MediaType.APPLICATION_FORM_URLENCODED
            }.bodyValue("grant_type=client_credentials")
            .retrieve()
            .bodyToMono<String>()
            .map { raw ->
                val json = objectMapper.readTree(raw)
                val token = json.get("access_token").asString()
                token
            }

    private fun postCheckout(body: Any): Mono<JsonNode> =
        getAccessToken().flatMap { token ->
            client
                .post()
                .uri("/v2/checkout/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(body)
                .exchangeToMono { handlePayPalResponse(it) }
        }

    private fun handlePayPalResponse(response: ClientResponse): Mono<JsonNode> =
        response.bodyToMono<String>().flatMap { raw ->
            if (!response.statusCode().is2xxSuccessful) {
                return@flatMap Mono.error(RuntimeException("PayPal error: $raw"))
            }

            val json = objectMapper.readTree(raw)
            Mono.just(json as JsonNode)
        }

    fun createPayPalOrder(amount: String): Mono<String> {
        val body =
            mapOf(
                "intent" to "CAPTURE",
                "purchase_units" to
                    listOf(mapOf("amount" to mapOf("currency_code" to "USD", "value" to amount))),
            )

        return postCheckout(body).map { json -> json["id"].asString() }
    }

    /**
     * Gets the capture ID if the user confirmed and processed their payment through PayPal.
     *
     * @return The captureId
     */
    fun captureOrder(paypalOrderId: String): String {
        val captureResponseJson =
            getAccessToken()
                .flatMap { token ->
                    client
                        .post()
                        .uri("/v2/checkout/orders/$paypalOrderId/capture")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .exchangeToMono { handlePayPalResponse(it) }
                }.block() ?: error("PayPal capture returned null")

        val dto = objectMapper.treeToValue<PayPalCaptureResponse>(captureResponseJson)
        val completedCapture =
            dto.purchaseUnits
                .flatMap { it.payments.captures }
                .firstOrNull { it.status == "COMPLETED" }
                ?: throw BadRequestException("PayPal captures have no completed status.")

        return completedCapture.id
    }
}
