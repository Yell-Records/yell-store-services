package com.yellrecords.services.mail

import com.yellrecords.services.config.CorsProps
import com.yellrecords.services.order.Order
import com.yellrecords.services.user.UserRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    private val userRepository: UserRepository,
    private val corsProps: CorsProps,
) {
    /**
     * Sends an "Order received" email to the site merchant.
     *
     * @param order Order details to use in email HTML template construction.
     */
    fun sendSellerInitialEmail(order: Order) {
        val siteUrl = corsProps.allowedOrigins.first()
        val context = baseEmailContext(order, siteUrl)

        val subjectLine = "New Order Received"
        val htmlBody = templateEngine.process(EmailSubject.SELLER_RECEIVED.templateName, context)

        sendEmail(to = getAdminEmail(), subjectLine, htmlBody)
    }

    /**
     * Sends an email to the buyer of an order.
     *
     * @param order Order details to use in HTML template construction. The
     *   [buyerEmail][Order.buyerEmail] property is the recipient of the email.
     * @param emailSubject The type of email to send.
     * @see EmailSubject
     */
    fun sendBuyerEmail(
        order: Order,
        emailSubject: EmailSubject,
    ) {
        val siteUrl = corsProps.allowedOrigins.first()
        val context = buyerEmailContext(order, siteUrl)

        val subjectLine =
            if (emailSubject == EmailSubject.BUYER_CANCELED) {
                "Your order was canceled"
            } else {
                emailSubject.with(order.orderNumber!!)
            }
        val htmlBody = templateEngine.process(emailSubject.templateName, context)

        sendEmail(to = order.buyerEmail, subjectLine, htmlBody)
    }

    private fun sendEmail(
        to: String,
        subject: String,
        body: String,
    ) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true)
        helper.setFrom("Yell Records <${getAdminEmail()}>")

        mailSender.send(message)
    }

    private fun getAdminEmail() = userRepository.findFirstAdmin()?.email ?: error("Cannot locate administrator email.")

    companion object {
        /** Link to front-end logo. */
        private const val LOGO_URL = "assets/yell-records-banner.png"

        /**
         * Constructs a base email context.
         *
         * @param order Order to use for template construction.
         * @param websiteLink URL to the website.
         * @return Email context with variables set
         */
        private fun baseEmailContext(
            order: Order,
            websiteLink: String,
        ): Context {
            val context = Context()

            context.setVariable("orderNumber", order.orderNumber!!)
            context.setVariable("dateAdded", order.createdAt.toLocalDate())
            context.setVariable("subTotal", order.subtotal)
            context.setVariable("shippingCost", order.shippingCost)
            context.setVariable("total", order.totalPaid)
            context.setVariable("websiteUrl", websiteLink)
            context.setVariable("logoUrl", "$websiteLink/$LOGO_URL")

            val products =
                order.orderItems.map { item ->
                    val itemTotal = item.listingPrice * item.quantity.toBigDecimal()

                    EmailProduct(
                        name = item.listingTitle,
                        listingId = item.listingId,
                        quantity = item.quantity,
                        price = item.listingPrice,
                        total = itemTotal,
                    )
                }

            context.setVariable("products", products)

            return context
        }

        /**
         * Extends the base email context construction with other necessary variables for buyers to
         * see.
         *
         * @param order Order to use for template construction.
         * @param websiteLink URL to the website.
         * @return Extended email context with variables set.
         * @see baseEmailContext
         */
        private fun buyerEmailContext(
            order: Order,
            websiteLink: String,
        ): Context {
            val context = baseEmailContext(order, websiteLink)

            context.setVariable("email", order.buyerEmail)
            context.setVariable("phone", order.shippingPhone)
            context.setVariable("trackingNo", order.trackingNumber)

            val name = "${order.shippingFirstName} ${order.shippingLastName}"
            context.setVariable("name", name)
            context.setVariable("addr1", order.shippingAddressLine1)

            if (order.shippingAddressLine2 != null && order.shippingAddressLine2 != "") {
                context.setVariable("addr2", order.shippingAddressLine2)
            }

            val cityStateZip =
                "${order.shippingCity}, ${order.shippingState} ${order.shippingPostalCode}"
            context.setVariable("cityStateZip", cityStateZip)

            return context
        }
    }
}
