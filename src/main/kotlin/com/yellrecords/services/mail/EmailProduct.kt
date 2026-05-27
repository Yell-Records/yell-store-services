package com.yellrecords.services.mail

import java.math.BigDecimal
import java.util.UUID

/** Structure of an item listing displayed in an email. */
data class EmailProduct(
    val name: String,
    val listingId: UUID,
    val quantity: Int,
    val price: BigDecimal,
    val total: BigDecimal,
)
