package com.yellrecords.services.cart.dto

import com.yellrecords.services.itemlisting.dto.ItemListingDto
import java.util.UUID

data class AddCartItemDto(
    val guestSessionId: UUID,
    val listingInfo: ItemListingDto,
    val itemQuantity: Int,
)
