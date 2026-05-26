package com.yellrecords.services.mail

import java.math.BigDecimal

/** Structure of an item listing displayed in an email. */
data class EmailProduct(
    val name: String,
    val quantity: Int,
    val price: BigDecimal,
    val total: BigDecimal,
)
