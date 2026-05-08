package com.yellrecords.services.paypal

import com.yellrecords.services.config.PayPalProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper

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

    fun post(
        path: String,
        body: Any,
    ): Mono<JsonNode> =
        getAccessToken().flatMap { token ->
            client
                .post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(body)
                .exchangeToMono { response ->
                    response.bodyToMono<String>().flatMap { raw ->
                        if (!response.statusCode().is2xxSuccessful) {
                            return@flatMap Mono.error(RuntimeException("PayPal error: $raw"))
                        }

                        val json = objectMapper.readTree(raw)
                        Mono.just(json as JsonNode)
                    }
                }
        }
}
