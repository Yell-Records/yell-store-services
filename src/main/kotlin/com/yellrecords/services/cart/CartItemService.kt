package com.yellrecords.services.cart

import com.yellrecords.services.cart.dto.AddCartItemDto
import com.yellrecords.services.cart.dto.CartItemWithListingDto
import com.yellrecords.services.category.CategoryRepository
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.itemlisting.ItemListingRepository
import com.yellrecords.services.itemlisting.dto.ItemListingMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class CartItemService(
    private val cartItemRepo: CartItemRepository,
    private val itemListingRepo: ItemListingRepository,
    private val categoryRepository: CategoryRepository,
) {
    /** Retrieves a list of cart items associated with a guest session ID. */
    fun getCartItemsByGuestId(guestId: UUID): List<CartItemWithListingDto> {
        val cartItems =
            cartItemRepo.findGuestCartItems(guestId).ifEmpty {
                return emptyList()
            }

        return mappedCartItems(cartItems)
    }

    /** Clears items associated with a guest session ID. */
    @Transactional
    fun deleteGuestCartItems(guestId: UUID) = cartItemRepo.deleteCartItemsByGuestSessionId(guestId)

    @Transactional
    fun deleteGuestCartItem(
        guestId: UUID,
        listingId: UUID,
    ) = cartItemRepo.deleteGuestItem(guestId, listingId)

    /**
     * Creates a new cart item entry in the database. If the listing already exists under the
     * requested guest session ID, the quantity of the existing item is increased by `itemQuantity`.
     */
    fun addItemToCart(itemInfo: AddCartItemDto): CartItemWithListingDto {
        if (itemInfo.itemQuantity < 1) {
            throw BadRequestException("Cart item quantity must be greater than 0.")
        }

        val existingItem =
            cartItemRepo.findGuestCartItems(itemInfo.guestSessionId).firstOrNull {
                it.listingId == itemInfo.listingInfo.id
            }

        return existingItem?.let { item ->
            // Update the existing item by adding the requested quantity to it
            item.quantity += itemInfo.itemQuantity
            item.updatedAt = OffsetDateTime.now()

            val newCartItem = cartItemRepo.save(item)

            CartItemWithListingDto(
                id = newCartItem.id!!,
                quantity = item.quantity,
                itemListing = itemInfo.listingInfo,
            )
        }
            ?: run {
                val itemEntity =
                    CartItem(
                        guestSessionId = itemInfo.guestSessionId,
                        listingId = itemInfo.listingInfo.id,
                        quantity = itemInfo.itemQuantity,
                    )

                val savedCartItem = cartItemRepo.save(itemEntity)

                CartItemWithListingDto(
                    id = savedCartItem.id!!,
                    quantity = savedCartItem.quantity,
                    itemListing = itemInfo.listingInfo,
                )
            }
    }

    private fun mappedCartItems(cartItems: List<CartItem>): List<CartItemWithListingDto> {
        val listingIds = cartItems.map { it.listingId }

        // Retrieve information on each listing
        val itemListings = itemListingRepo.findAllById(listingIds).associateBy { it.id }

        return cartItems.map { cartItem ->
            val listing = itemListings[cartItem.listingId]!!
            val category = categoryRepository.findById(listing.categoryId).get()

            CartItemWithListingDto(
                id = cartItem.id!!,
                quantity = cartItem.quantity,
                itemListing = ItemListingMapper.toDto(listing, category),
            )
        }
    }
}
