package com.yellrecords.services.orderitem.dto

import java.math.BigDecimal
import java.util.UUID

data class OrderItemDto(
    val id: UUID,
    val listingId: UUID,
    val listingTitle: String,
    val listingDescription: String?,
    val listingImageUrl: String?,
    val listingPrice: BigDecimal,
    val quantity: Int,
)
