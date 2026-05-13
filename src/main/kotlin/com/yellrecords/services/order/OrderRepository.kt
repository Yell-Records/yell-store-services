package com.yellrecords.services.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.OffsetDateTime
import java.util.UUID

interface OrderRepository : JpaRepository<Order, UUID> {
    @Query(
        """
            SELECT o FROM Order o
            WHERE o.status = 'IN_PROGRESS'
                OR o.status = 'PAID'
            ORDER BY o.paidAt DESC
        """,
    )
    fun findUnfinishedOrders(): List<Order>

    @Query(
        """
            SELECT o FROM Order o
            WHERE o.status != 'IN_PROGRESS'
                AND o.status != 'AWAITING_PAYMENT'
                AND o.status != 'PAID'
            ORDER BY o.shippedAt DESC
        """,
    )
    fun findFinishedOrders(): List<Order>

    @Query(
        """
            SELECT o FROM Order o
            WHERE o.status = 'AWAITING_PAYMENT'
                AND o.createdAt < :cutoff
        """,
    )
    fun findAllToClean(cutoff: OffsetDateTime): List<Order>
}
