package com.yellrecords.services.order

import com.yellrecords.services.orderitem.OrderItem
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @Column(name = "buyer_email") var buyerEmail: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.AWAITING_PAYMENT,
    @Column(name = "total_paid", nullable = false) var totalPaid: BigDecimal,
    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "shipping_first_name", nullable = false) var shippingFirstName: String,
    @Column(name = "shipping_last_name", nullable = false) var shippingLastName: String,
    @Column(name = "shipping_address_line1", nullable = false) var shippingAddressLine1: String,
    @Column(name = "shipping_address_line2") var shippingAddressLine2: String? = null,
    @Column(name = "shipping_city", nullable = false) var shippingCity: String,
    @Column(name = "shipping_state", nullable = false) var shippingState: String,
    @Column(name = "shipping_postal_code", nullable = false) var shippingPostalCode: String,
    @Column(name = "shipping_phone", nullable = false) var shippingPhone: String,
    @Column(name = "paid_at") var paidAt: OffsetDateTime? = null,
    @Column(name = "tracking_number") var trackingNumber: String? = null,
    @Column(name = "tracking_carrier") var trackingCarrier: String? = null,
    @Column(name = "shipped_at") var shippedAt: OffsetDateTime? = null,
    @Column(name = "paypal_order_id") var paypalOrderId: String? = null,
    @Column(name = "paypal_capture_id") var paypalCaptureId: String? = null,
    @Column(name = "guest_session_id", nullable = false) var guestSessionId: UUID,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var orderItems: MutableList<OrderItem> = mutableListOf(),
)
