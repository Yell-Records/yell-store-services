package com.yellrecords.services.cart

import com.yellrecords.services.cart.dto.AddCartItemDto
import com.yellrecords.services.cart.dto.CartItemWithListingDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/cart-items")
class CartItemController(
    private val cartService: CartItemService,
) {
    @GetMapping("/guest/{guestSessionId}")
    fun getGuestCartItems(
        @PathVariable guestSessionId: UUID,
    ): List<CartItemWithListingDto> = cartService.getCartItemsByGuestId(guestSessionId)

    @PostMapping
    fun addItemToCart(
        @RequestBody addItemReq: AddCartItemDto,
    ): CartItemWithListingDto = cartService.addItemToCart(addItemReq)

    @DeleteMapping("/guest/{guestSessionId}")
    fun clearGuestCartItems(
        @PathVariable guestSessionId: UUID,
    ): ResponseEntity<Void> {
        cartService.deleteGuestCartItems(guestSessionId)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/guest/{guestSessionId}/listing/{listingId}")
    fun deleteCartItemFromGuest(
        @PathVariable guestSessionId: UUID,
        @PathVariable listingId: UUID,
    ): ResponseEntity<Void> {
        cartService.deleteGuestCartItem(guestSessionId, listingId)

        return ResponseEntity.noContent().build()
    }
}
