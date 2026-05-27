package com.yellrecords.services.mail

/**
 * Preset email definitions.
 *
 * @property templateName Filename (no ext.) of the HTML template associated with the subject.
 * @property format Subject line string template format.
 */
enum class EmailSubject(
    val templateName: String,
    private val format: String,
) {
    BUYER_RECEIVED(templateName = "buyer-initial-order", format = "Order #%s Received"),
    BUYER_CONFIRMED(templateName = "buyer-confirmed-order", format = "Order #%s Confirmed"),
    BUYER_SHIPPED(templateName = "buyer-shipped-order", format = "Order #%s Shipped"),
    BUYER_CANCELED(templateName = "buyer-canceled-order", format = "Order #%s Canceled"),
    SELLER_RECEIVED(templateName = "seller-order", format = "Order #%s Received"),
    ;

    /**
     * Formats the email subject as a string.
     *
     * @param orderNumber Order number to format subject with.
     * @return Formatted subject line.
     */
    fun with(orderNumber: Long): String = format.format(orderNumber)
}
