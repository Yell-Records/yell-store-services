package com.yellrecords.services.order

import com.yellrecords.services.cart.CartItemService
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.exception.ForbiddenException
import com.yellrecords.services.order.dto.CreateOrderRequestDto
import com.yellrecords.services.order.dto.OrderDto
import com.yellrecords.services.order.mapper.OrderMapper
import com.yellrecords.services.orderitem.mapper.OrderItemMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartItemService: CartItemService,
) {
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

    /**
     * Saves a new order and its items to the database using the buyer's associated cart items. Once
     * an order is made, it clears the cart items being held by the buyer.
     *
     * @throws BadRequestException If the buyer's cart is empty.
     * @throws ForbiddenException If the buyer doesn't have enough money to make the order.
     */
    @Transactional
    fun createOrder(orderInfo: CreateOrderRequestDto): OrderDto {
        // Total paid must be greater than 0
        if (orderInfo.totalPaid <= BigDecimal.ZERO) {
            throw BadRequestException("Total paid must be greater than 0.")
        }

        val cartItems = cartItemService.getCartItemsByGuestId(orderInfo.guestSessionId)
        cartItems.ifEmpty { throw BadRequestException("Order has no items!") }

        // TODO Process payments here

        val orderEntity = OrderMapper.asNewEntity(orderInfo, OffsetDateTime.now())

        // Convert all cart items from the client into order items
        cartItems.forEach { cartItemDto ->
            val itemEntity = OrderItemMapper.asNewEntity(cartItemDto)

            itemEntity.order = orderEntity
            orderEntity.orderItems.add(itemEntity)
        }

        val savedOrder = orderRepository.save(orderEntity)

        // Clear the client's cart
        cartItemService.deleteGuestCartItems(orderInfo.guestSessionId)

        return OrderMapper.toDto(savedOrder)
    }
}
