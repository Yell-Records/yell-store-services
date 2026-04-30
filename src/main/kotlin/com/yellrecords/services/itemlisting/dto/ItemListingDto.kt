package com.yellrecords.services.itemlisting.dto

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class ItemListingDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val price: BigDecimal,
    val imageUrl: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val isActive: Boolean,
    val quantitySold: Int,
    val categorySlug: String,
    val categoryName: String,
)
