package com.yellrecords.services.order

import com.yellrecords.services.order.dto.CreateOrderRequestDto
import com.yellrecords.services.order.dto.OrderDto
import com.yellrecords.services.order.dto.TrackingDetailsDto
import com.yellrecords.services.order.dto.UpdateOrderDto
import com.yellrecords.services.paypal.PayPalOrderResponse
import com.yellrecords.services.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @GetMapping
    @RolesAllowed(UserRole.ADMIN)
    fun getOrdersForAdmin(
        @RequestParam(required = true) unfinished: Boolean,
    ): List<OrderDto> = orderService.getOrdersForSeller(unfinished)

    @GetMapping("/{id}")
    @RolesAllowed(UserRole.ADMIN)
    fun getOrder(
        @PathVariable id: UUID,
    ): OrderDto = orderService.getOrder(id)

    @GetMapping("/order-number/{orderNumber}")
    @RolesAllowed(UserRole.ADMIN)
    fun getOrderFromNumber(
        @PathVariable orderNumber: Long,
    ): OrderDto = orderService.getOrderByOrderNumber(orderNumber)

    @PostMapping("/{id}/paypal/create")
    fun createPaypalOrder(
        @PathVariable id: UUID,
    ): PayPalOrderResponse = orderService.createPayPalOrder(id)

    @PostMapping
    fun createOrder(
        @RequestBody orderReq: CreateOrderRequestDto,
    ): ResponseEntity<OrderDto> {
        val orderWithItems = orderService.createOrder(orderReq)

        return ResponseEntity(orderWithItems, HttpStatus.CREATED)
    }

    @PostMapping("/{id}/paypal/capture")
    fun capturePayment(
        @PathVariable id: UUID,
    ): OrderDto = orderService.captureOrder(id)

    @PatchMapping("/{id}")
    fun updateOrder(
        @PathVariable id: UUID,
        @RequestBody updateOrderDto: UpdateOrderDto,
    ): OrderDto = orderService.updateOrder(id, updateOrderDto)

    @PatchMapping("/{id}/confirm")
    @RolesAllowed(UserRole.ADMIN)
    fun confirmOrder(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> = orderService.confirmOrder(id)

    @PatchMapping("/{id}/shipped")
    @RolesAllowed(UserRole.ADMIN)
    fun shipOrder(
        @PathVariable id: UUID,
        @RequestBody trackingDetails: TrackingDetailsDto,
    ): ResponseEntity<Void> = orderService.shipOrder(id, trackingDetails)

    @PatchMapping("/{id}/fulfill")
    @RolesAllowed(UserRole.ADMIN)
    fun fulfillOrder(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> = orderService.fulfillOrder(id)

    @PatchMapping("/{id}/cancel")
    @RolesAllowed(UserRole.ADMIN)
    fun cancelOrder(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> = orderService.cancelOrder(id)

    @PatchMapping("/{id}/anonymize")
    @RolesAllowed(UserRole.ADMIN)
    fun anonymizeOrder(
        @PathVariable id: UUID,
    ): ResponseEntity<OrderDto> = orderService.anonymizeOrder(id)
}
