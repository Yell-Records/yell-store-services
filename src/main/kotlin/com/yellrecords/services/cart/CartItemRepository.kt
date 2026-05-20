package com.yellrecords.services.cart

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.OffsetDateTime
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

    @Query(
        """
            SELECT ci FROM CartItem ci
            WHERE ci.guestSessionId IN (
                SELECT ci2.guestSessionId FROM CartItem ci2
                GROUP BY ci2.guestSessionId
                HAVING MAX(ci2.updatedAt) <= :cutoff
            )
        """,
    )
    fun findCartItemsToClean(cutoff: OffsetDateTime): List<CartItem>
}
