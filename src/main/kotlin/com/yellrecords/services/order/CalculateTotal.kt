package com.yellrecords.services.order

import java.math.BigDecimal

/** Sums up the total price of the order. */
fun Order.total(): BigDecimal = this.subtotal + this.shippingCost
