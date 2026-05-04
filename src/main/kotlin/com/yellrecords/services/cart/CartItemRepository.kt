package com.yellrecords.services.cart

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CartItemRepository : JpaRepository<CartItem, UUID> {
    @Query(
        """
            SELECT ci FROM CartItem ci
            WHERE ci.guestSessionId = :guestId
        """,
    )
    fun findGuestCartItems(guestId: UUID): List<CartItem>

    fun deleteCartItemsByGuestSessionId(guestSessionId: UUID)

    @Modifying
    @Query(
        """
            DELETE FROM CartItem ci
            WHERE ci.guestSessionId = :guestSessionId
                AND ci.listingId = :listingId
        """,
    )
    fun deleteGuestItem(
        guestSessionId: UUID,
        listingId: UUID,
    )
}
