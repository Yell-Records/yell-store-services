package com.yellrecords.services.itemlisting

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ItemListingRepository : JpaRepository<ItemListing, UUID> {
    @Query(
        """
            SELECT i FROM ItemListing i
            WHERE i.categoryId = :categoryId
                AND i.isActive
        """,
    )
    fun findByCategoryId(categoryId: UUID): List<ItemListing>

    @Query(
        """
        SELECT l FROM ItemListing l WHERE l.isActive
    """,
    )
    fun findAllActive(): List<ItemListing>
}
