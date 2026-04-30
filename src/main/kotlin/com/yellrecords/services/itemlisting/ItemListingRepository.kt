package com.yellrecords.services.itemlisting

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ItemListingRepository : JpaRepository<ItemListing, UUID> {
    fun findByCategoryId(categoryId: UUID): List<ItemListing>
}
