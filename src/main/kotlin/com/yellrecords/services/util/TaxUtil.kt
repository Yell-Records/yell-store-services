package com.yellrecords.services.util

import java.math.BigDecimal
import java.math.RoundingMode

object TaxUtil {
    val ILLINOIS_TAX_RATE = BigDecimal("0.10")

    /**
     * Calculates tax.
     *
     * Company note: Since there is no economic nexus in anywhere but Illinois, only shipments going
     * to Illinois are affected.
     *
     * @param shippingState Two-character state code of shipment
     * @param subtotal Cost of items
     * @return Tax amount
     * @see ILLINOIS_TAX_RATE
     */
    fun calculateTax(
        shippingState: String,
        subtotal: BigDecimal,
    ): BigDecimal =
        if (shippingState == "IL") {
            subtotal.multiply(ILLINOIS_TAX_RATE).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
}
