package com.yellrecords.services.order.mapper

import com.yellrecords.services.order.Order
import com.yellrecords.services.order.dto.CreateOrderRequestDto
import com.yellrecords.services.order.dto.OrderDto
import com.yellrecords.services.orderitem.mapper.OrderItemMapper
import com.yellrecords.services.util.ShippingUtil
import com.yellrecords.services.util.TaxUtil

object OrderMapper {
    fun toDto(entity: Order) =
        OrderDto(
            id = entity.id!!,
            buyerEmail = entity.buyerEmail,
            status = entity.status,
            subtotal = entity.subtotal,
            shippingCost = entity.shippingCost,
            tax = entity.tax,
            totalPaid = entity.totalPaid,
            createdAt = entity.createdAt,
            shippingFirstname = entity.shippingFirstName,
            shippingLastname = entity.shippingLastName,
            shippingAddressLine1 = entity.shippingAddressLine1,
            shippingAddressLine2 = entity.shippingAddressLine2,
            shippingCity = entity.shippingCity,
            shippingState = entity.shippingState,
            shippingPostalCode = entity.shippingPostalCode,
            shippingPhone = entity.shippingPhone,
            trackingNumber = entity.trackingNumber,
            trackingCarrier = entity.trackingCarrier,
            orderItems = entity.orderItems.map { OrderItemMapper.toDto(it) },
            shippedAt = entity.shippedAt,
            paidAt = entity.paidAt,
            orderId = entity.orderNumber!!,
        )

    fun asNewEntity(dto: CreateOrderRequestDto) =
        Order(
            guestSessionId = dto.guestSessionId,
            buyerEmail = dto.buyerEmail,
            subtotal = dto.subtotal,
            tax = TaxUtil.calculateTax(dto.shippingState, dto.subtotal),
            shippingCost = ShippingUtil.FLAT_SHIPPING_COST,
            shippingFirstName = dto.shippingFirstName,
            shippingLastName = dto.shippingLastName,
            shippingAddressLine1 = dto.shippingAddressLine1,
            shippingAddressLine2 = dto.shippingAddressLine2,
            shippingCity = dto.shippingCity,
            shippingState = dto.shippingState,
            shippingPostalCode = dto.shippingPostalCode,
            shippingPhone = dto.shippingPhone,
        )
}
