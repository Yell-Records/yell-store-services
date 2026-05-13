package com.yellrecords.services.paypal

import com.fasterxml.jackson.annotation.JsonProperty

data class PayPalCaptureResponse(
    @JsonProperty("purchase_units") val purchaseUnits: List<PurchaseUnit>,
) {
    data class PurchaseUnit(
        val payments: Payments,
    )

    data class Payments(
        val captures: List<Capture>,
    )

    data class Capture(
        val id: String,
        val status: String,
    )
}
