package com.yellrecords.services.orderitem.mapper

import com.yellrecords.services.cart.dto.CartItemWithListingDto
import com.yellrecords.services.orderitem.OrderItem
import com.yellrecords.services.orderitem.dto.OrderItemDto

object OrderItemMapper {
    fun toDto(entity: OrderItem) =
        OrderItemDto(
            id = entity.id!!,
            listingId = entity.listingId,
            quantity = entity.quantity,
            listingPrice = entity.listingPrice,
            listingTitle = entity.listingTitle,
            listingDescription = entity.listingDescription,
            listingImageUrl = entity.listingImageUrl,
        )

    fun asNewEntity(cartItemDto: CartItemWithListingDto) =
        OrderItem(
            listingId = cartItemDto.itemListing.id,
            quantity = cartItemDto.quantity,
            listingPrice = cartItemDto.itemListing.price,
            listingTitle = cartItemDto.itemListing.title,
            listingDescription = cartItemDto.itemListing.description,
            listingImageUrl = cartItemDto.itemListing.imageUrl,
        )
}
