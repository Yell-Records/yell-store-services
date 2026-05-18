package com.yellrecords.services.order

/** Status indicator for [Order] entities. */
enum class OrderStatus {
    /**
     * Order state: `1/5`
     *
     * User has provided shipping info but has not authorized payment.
     * - Next: [PAID]
     */
    AWAITING_PAYMENT,

    /**
     * Order state: `2/5`
     *
     * User has authorized payment and is awaiting action from the merchant.
     * - Previous: [AWAITING_PAYMENT]
     * - Next: [IN_PROGRESS]
     */
    PAID,

    /**
     * Order state: `3/5`
     *
     * Merchant confirmed the order and has begun assembling purchased items.
     * - Previous: [PAID]
     * - Next: [SHIPPED]
     */
    IN_PROGRESS,

    /**
     * Order state: `4/5`
     *
     * Merchant has provided shipment information for package containing ordered items.
     * - Previous: [IN_PROGRESS]
     * - Next: [FULFILLED]
     */
    SHIPPED,

    /**
     * Order state: `5/5`
     *
     * Merchant received confirmation that package was delivered. This status is manually set by the
     * merchant.
     * - Previous: [SHIPPED]
     */
    FULFILLED,

    /**
     * Merchant has acknowledged the Order could not be fulfilled.
     * - Previous: [PAID]
     */
    CANCELED,
}
