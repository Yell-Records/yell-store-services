package com.yellrecords.services.orderitem

import com.yellrecords.services.order.Order
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id") var order: Order? = null,
    @Column(name = "listing_id", nullable = false) var listingId: UUID,
    @Column(name = "listing_title", nullable = false) var listingTitle: String,
    @Column(name = "listing_description") var listingDescription: String? = null,
    @Column(name = "listing_image_url") var listingImageUrl: String? = null,
    @Column(name = "listing_price", nullable = false) var listingPrice: BigDecimal,
    @Column(nullable = false) var quantity: Int,
)
