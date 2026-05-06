package com.yellrecords.services.cart

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.cart.dto.AddCartItemDto
import com.yellrecords.services.itemlisting.dto.ItemListingDto
import com.yellrecords.services.itemlisting.dto.ItemListingMapper
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class CartItemControllerTest : BaseH2Test() {
    companion object {
        const val BASE_PATH = "/api/cart-items"
    }

    @Autowired lateinit var cartItemRepository: CartItemRepository

    private lateinit var listing1Dto: ItemListingDto
    private lateinit var listing2Dto: ItemListingDto

    private val guestId = UUID.randomUUID()

    private lateinit var guestCartItem: CartItem

    @BeforeEach
    fun addCartItems() {
        val listings = super.initListings()
        val listing1 = listings.last()

        listing1Dto = ItemListingMapper.toDto(listing1, listing1.category()!!)

        val listing2 = listings.first()
        listing2Dto = ItemListingMapper.toDto(listing2, listing2.category()!!)

        val guestItem = CartItem(guestSessionId = guestId, listingId = listing1.id!!, quantity = 1)

        guestCartItem = cartItemRepository.save(guestItem)
    }

    @Nested
    inner class AddItem {
        @Test
        fun `should add item to guest cart`() {
            val preItems = cartItemRepository.findGuestCartItems(guestId)
            preItems shouldHaveSize 1
            preItems.forNone { it.listingId shouldBe listing2Dto.id }

            val req =
                AddCartItemDto(
                    guestSessionId = guestId,
                    listingInfo = listing2Dto,
                    itemQuantity = 1,
                )

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = req)
                .andExpect(status().isOk)

            val guestItems = cartItemRepository.findGuestCartItems(guestId)
            guestItems shouldHaveSize 2
            guestItems.forOne { it.listingId shouldBe listing2Dto.id }
        }

        @Test
        fun `should increment quantity of item in guest cart`() {
            val req =
                AddCartItemDto(
                    guestSessionId = guestId,
                    listingInfo = listing1Dto,
                    itemQuantity = 1,
                )

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = req)
                .andExpect(status().isOk)

            val guestItems = cartItemRepository.findGuestCartItems(guestId)
            guestItems shouldHaveSize 1

            val guestItem = guestItems.first()
            guestItem.quantity shouldBe 2
            guestItem.updatedAt shouldBeGreaterThan guestItem.createdAt
        }
    }

    @Nested
    inner class RemoveItems {
        @Test
        fun `should remove all items from guest cart`() {
            val preItems = cartItemRepository.findGuestCartItems(guestId)
            preItems shouldHaveAtLeastSize 1

            mockRequest(requestType = DELETE, path = "$BASE_PATH/guest/$guestId", token = null)
                .andExpect(status().isNoContent)

            val guestItems = cartItemRepository.findGuestCartItems(guestId)
            guestItems shouldHaveSize 0
        }

        @Test
        fun `should remove single item from guest cart`() {
            // Add second item to guest cart
            val createReq =
                AddCartItemDto(
                    guestSessionId = guestId,
                    listingInfo = listing2Dto,
                    itemQuantity = 1,
                )

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = createReq)
                .andExpect(status().isOk)

            val preItems = cartItemRepository.findGuestCartItems(guestId)
            preItems shouldHaveSize 2
            preItems.forOne { it.listingId shouldBe listing2Dto.id }

            mockRequest(
                requestType = DELETE,
                path = "$BASE_PATH/guest/$guestId/listing/${listing2Dto.id}",
                token = null,
            ).andExpect(status().isNoContent)

            val guestItems = cartItemRepository.findGuestCartItems(guestId)
            guestItems shouldHaveSize 1
            guestItems.forNone { it.listingId shouldBe listing2Dto.id }
        }
    }

    @Nested
    inner class GetCartItems {
        @Test
        fun `should get items from guest cart`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/guest/$guestId", token = null)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(1))
        }
    }
}
