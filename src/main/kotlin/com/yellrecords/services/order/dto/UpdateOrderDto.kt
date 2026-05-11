package com.yellrecords.services.order.dto

import java.util.UUID

data class UpdateOrderDto(
    val guestSessionId: UUID,
    val buyerEmail: String? = null,
    val shippingFirstName: String? = null,
    val shippingLastName: String? = null,
    val shippingAddressLine1: String? = null,
    val shippingAddressLine2: String? = null,
    val shippingCity: String? = null,
    val shippingState: String? = null,
    val shippingPostalCode: String? = null,
    val shippingPhone: String? = null,
)
