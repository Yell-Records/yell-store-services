package com.yellrecords.services.order.dto

import java.math.BigDecimal
import java.util.UUID

/** Structure for incoming requests to create a new order. */
data class CreateOrderRequestDto(
    val guestSessionId: UUID,
    val buyerEmail: String,
    val subtotal: BigDecimal,
    val shippingFirstName: String,
    val shippingLastName: String,
    val shippingAddressLine1: String,
    val shippingAddressLine2: String?,
    val shippingCity: String,
    val shippingState: String,
    val shippingPostalCode: String,
    val shippingPhone: String,
)
