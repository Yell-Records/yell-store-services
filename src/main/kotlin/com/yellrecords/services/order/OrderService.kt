package com.yellrecords.services.order

import com.yellrecords.services.cart.CartItemService
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.exception.ConflictException
import com.yellrecords.services.exception.ForbiddenException
import com.yellrecords.services.exception.NotFoundException
import com.yellrecords.services.order.dto.CreateOrderRequestDto
import com.yellrecords.services.order.dto.OrderDto
import com.yellrecords.services.order.dto.UpdateOrderDto
import com.yellrecords.services.order.mapper.OrderMapper
import com.yellrecords.services.orderitem.mapper.OrderItemMapper
import com.yellrecords.services.paypal.PayPalClient
import com.yellrecords.services.paypal.PayPalOrderResponse
import com.yellrecords.services.util.TaxUtil
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartItemService: CartItemService,
    private val paypalClient: PayPalClient,
) {
    @PersistenceContext private lateinit var entityManager: EntityManager

    /** Retrieves all orders with only order items associated by the seller. */
    fun getOrdersForSeller(unfinished: Boolean): List<OrderDto> {
        val orders =
            if (unfinished) {
                orderRepository.findUnfinishedOrders()
            } else {
                orderRepository.findFinishedOrders()
            }

        return orders.map { order -> OrderMapper.toDto(order) }
    }

    fun getOrder(orderId: UUID): OrderDto {
        val order =
            orderRepository.findById(orderId).getOrElse {
                throw NotFoundException("Order with ID $orderId not found.")
            }

        return OrderMapper.toDto(order)
    }

    /**
     * Saves a new order and its items to the database using the buyer's associated cart items.
     *
     * @throws BadRequestException If the buyer's cart is empty.
     */
    @Transactional
    fun createOrder(orderInfo: CreateOrderRequestDto): OrderDto {
        if (orderInfo.subtotal <= BigDecimal.ZERO) {
            throw BadRequestException("Subtotal must be greater than 0.")
        }

        val cartItems = cartItemService.getCartItemsByGuestId(orderInfo.guestSessionId)
        cartItems.ifEmpty { throw BadRequestException("Order has no items!") }

        val orderEntity = OrderMapper.asNewEntity(orderInfo)

        // Convert all cart items from the client into order items
        cartItems.forEach { cartItemDto ->
            val itemEntity = OrderItemMapper.asNewEntity(cartItemDto)

            itemEntity.order = orderEntity
            orderEntity.orderItems.add(itemEntity)
        }

        val savedOrder = orderRepository.save(orderEntity)
        // For order numbers, the database uses a number sequence that is not generated
        // on save, so we need to flush and refresh the entity to retrieve the next value.
        entityManager.flush()
        entityManager.refresh(savedOrder)

        return OrderMapper.toDto(savedOrder)
    }

    @Transactional
    fun captureOrder(orderId: UUID): OrderDto {
        val order =
            orderRepository.findById(orderId).getOrElse {
                throw NotFoundException("Order with id $orderId not found.")
            }

        ensureCorrectOrderState(order)

        val captureId = paypalClient.captureOrder(order.paypalOrderId!!)

        order.paypalCaptureId = captureId
        order.totalPaid = order.total()
        order.status = OrderStatus.PAID
        order.paidAt = OffsetDateTime.now()

        // Clear the client's cart items
        cartItemService.deleteGuestCartItems(order.guestSessionId)

        return OrderMapper.toDto(order)
    }

    @Transactional
    fun createPayPalOrder(orderId: UUID): PayPalOrderResponse {
        val order =
            orderRepository.findById(orderId).getOrElse {
                throw NotFoundException("Order with id $orderId not found.")
            }

        // Get PayPal information
        val paypalOrderId =
            paypalClient.createPayPalOrder(order).block()
                ?: throw BadRequestException("Failed to create PayPal order.")

        order.paypalOrderId = paypalOrderId

        return PayPalOrderResponse(paypalOrderId)
    }

    @Transactional
    @Suppress("DuplicatedCode") // For all the .let statements
    fun updateOrder(
        orderId: UUID,
        updates: UpdateOrderDto,
    ): OrderDto {
        val order =
            orderRepository.findById(orderId).getOrElse {
                throw NotFoundException("Order with id $orderId not found.")
            }

        if (order.guestSessionId != updates.guestSessionId) {
            throw ForbiddenException("Invalid session ID.")
        }

        if (order.status != OrderStatus.AWAITING_PAYMENT) {
            throw ForbiddenException("Cannot update due to status.")
        }

        // Update the item
        updates.buyerEmail?.let { order.buyerEmail = it }
        updates.shippingFirstName?.let { order.shippingFirstName = it }
        updates.shippingLastName?.let { order.shippingLastName = it }
        updates.shippingAddressLine1?.let { order.shippingAddressLine1 = it }

        order.shippingAddressLine2 = updates.shippingAddressLine2

        updates.shippingCity?.let { order.shippingCity = it }
        updates.shippingState?.let {
            order.shippingState = it
            order.tax = TaxUtil.calculateTax(it, order.subtotal)
        }
        updates.shippingPostalCode?.let { order.shippingPostalCode = it }
        updates.shippingPhone?.let { order.shippingPhone = it }

        return OrderMapper.toDto(order)
    }

    private fun ensureCorrectOrderState(order: Order) {
        // Prevent double-capture
        if (order.status == OrderStatus.PAID) {
            throw ConflictException("Order was already paid.")
        }

        // Ensure order is in correct state
        if (order.status != OrderStatus.AWAITING_PAYMENT) {
            throw BadRequestException("Order is not awaiting payment.")
        }

        if (order.paypalOrderId == null) {
            error("Order paypalOrderId was null.")
        }
    }
}
