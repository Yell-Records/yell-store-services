package com.yellrecords.services.order.dto

import com.yellrecords.services.order.OrderStatus
import com.yellrecords.services.orderitem.dto.OrderItemDto
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class OrderDto(
    val id: UUID,
    val orderNumber: Long,
    val buyerEmail: String,
    val status: OrderStatus,
    val subtotal: BigDecimal,
    val tax: BigDecimal,
    val shippingCost: BigDecimal,
    val totalPaid: BigDecimal?,
    val createdAt: OffsetDateTime,
    val shippingFirstname: String,
    val shippingLastname: String,
    val shippingAddressLine1: String,
    val shippingAddressLine2: String?,
    val shippingCity: String,
    val shippingState: String,
    val shippingPostalCode: String,
    val shippingPhone: String,
    val orderItems: List<OrderItemDto>,
    val trackingNumber: String?,
    val paidAt: OffsetDateTime?,
    val shippedAt: OffsetDateTime?,
)
