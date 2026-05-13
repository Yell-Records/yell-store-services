package com.yellrecords.services.paypal

import com.fasterxml.jackson.annotation.JsonProperty

/** Request body for creating a PayPal order. */
data class CreatePayPalOrderRequest(
    val intent: String = "CAPTURE",
    @JsonProperty("application_context")
    val applicationContext: ApplicationContext = ApplicationContext(),
    @JsonProperty("purchase_units") val purchaseUnits: List<PurchaseUnit>,
) {
    data class ApplicationContext(
        @JsonProperty("shipping_preference") val shippingPreference: String = "SET_PROVIDED_ADDRESS",
    )

    data class PurchaseUnit(
        val amount: Amount,
        val shipping: Shipping,
    ) {
        data class Amount(
            val value: String,
            @JsonProperty("currency_code") val currencyCode: String = "USD",
        )

        data class Shipping(
            val type: String = "SHIPPING",
            val name: ShippingName,
            val address: ShippingAddress,
        ) {
            data class ShippingName(
                @JsonProperty("full_name") val fullName: String,
            )

            data class ShippingAddress(
                @JsonProperty("address_line_1") val addressLine1: String,
                @JsonProperty("address_line_2") val addressLine2: String?,
                @JsonProperty("admin_area_2") val city: String,
                @JsonProperty("admin_area_1") val state: String,
                @JsonProperty("postal_code") val postalCode: String,
                @JsonProperty("country_code") val countryCode: String = "US",
            )
        }
    }
}
