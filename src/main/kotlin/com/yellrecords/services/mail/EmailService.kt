package com.yellrecords.services.mail

import com.yellrecords.services.order.Order
import com.yellrecords.services.user.UserService
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    private val userService: UserService,
) {
    fun sendEmail(
        to: String,
        subject: String,
        body: String,
    ) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true)

        mailSender.send(message)
    }

    fun sendSellerEmail(request: Order) {
        val context = Context()
        context.setVariable("orderItemId", request.orderNumber)
        context.setVariable("dateAdded", request.createdAt.toLocalDate())
        context.setVariable("orderStatus", request.status)
        context.setVariable("subTotal", request.subtotal)
        context.setVariable("shippingRate", request.shippingCost)
        context.setVariable("total", request.totalPaid)

        val products =
            request.orderItems.map { item ->
                mapOf(
                    "name" to item.listingTitle,
                    "quantity" to item.quantity,
                    "price" to item.listingPrice,
                    "total" to (item.listingPrice * item.quantity.toBigDecimal()),
                )
            }
        context.setVariable("products", products)
        val templateName = "sellerOrder"
        val subject = "Order " + request.orderNumber + " Received"
        val htmlBody = templateEngine.process(templateName, context)
        val to = userService.findAdmin().email ?: ""
        sendEmail(to, subject, htmlBody)
    }

    fun sendBuyerEmail(
        request: Order,
        templateName: String,
    ) {
        val to = request.buyerEmail
        val context = Context()
        context.setVariable("orderItemId", request.orderNumber)
        context.setVariable("dateAdded", request.createdAt.toLocalDate())
        context.setVariable("orderStatus", request.status)
        context.setVariable("subTotal", request.subtotal)
        context.setVariable("email", to)
        context.setVariable("phone", request.shippingPhone)
        context.setVariable("status", request.status)
        context.setVariable("shippingCost", request.shippingCost)
        context.setVariable("total", request.totalPaid)
        context.setVariable("trackingNo", request.trackingNumber)
        val name = request.shippingFirstName + " " + request.shippingLastName
        context.setVariable("name", name)
        context.setVariable("addr1", request.shippingAddressLine1)
        if (request.shippingAddressLine2 != null && request.shippingAddressLine2 != "") {
            context.setVariable("addr2", request.shippingAddressLine2)
        }
        val cityStateZip =
            request.shippingCity + ", " + request.shippingState + " " + request.shippingPostalCode
        context.setVariable("cityStateZip", cityStateZip)
        val products =
            request.orderItems.map { item ->
                mapOf(
                    "name" to item.listingTitle,
                    "quantity" to item.quantity,
                    "price" to item.listingPrice,
                    "total" to (item.listingPrice * item.quantity.toBigDecimal()),
                )
            }
        context.setVariable("products", products)
        var subject = ""
        when (templateName) {
            "buyerInitialOrder" -> {
                subject = "Order " + request.orderNumber + " Received"
            }

            "buyerConfirmOrder" -> {
                subject = "Order " + request.orderNumber + " Confirmed"
            }

            "buyerShippedOrder" -> {
                subject = "Order " + request.orderNumber + " Shipped"
            }

            "buyerCanceledOrder" -> {
                subject = "Order " + request.orderNumber + " Canceled"
            }
        }
        val htmlBody = templateEngine.process(templateName, context)

        sendEmail(to, subject, htmlBody)
    }
}
