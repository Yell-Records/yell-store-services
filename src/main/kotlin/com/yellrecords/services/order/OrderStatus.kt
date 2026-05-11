package com.yellrecords.services.order

/** Status indicator for Order entities. */
enum class OrderStatus {
    AWAITING_PAYMENT,
    PAID,
    IN_PROGRESS,
    SHIPPED,
    FULFILLED,
    CANCELED,
}
