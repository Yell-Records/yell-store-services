package com.yellrecords.services.itemlisting.dto

import com.yellrecords.services.category.Category
import com.yellrecords.services.itemlisting.ItemListing

object ItemListingMapper {
    /**
     * Converts an item listing database entity into its data transfer object.
     *
     * @param category Category associated with this item listing
     */
    fun toDto(
        entity: ItemListing,
        category: Category,
    ) = ItemListingDto(
        id = entity.id!!,
        title = entity.title,
        description = entity.description,
        price = entity.price,
        imageUrl = entity.imageUrl,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        isActive = entity.isActive,
        quantitySold = entity.quantitySold,
        categoryName = category.name,
        categorySlug = category.slug,
    )
}
