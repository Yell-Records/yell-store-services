package com.yellrecords.services.cart

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.itemlisting.ItemListing
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime
import java.util.UUID

class CartItemCleanupJobTest : BaseH2Test() {
    @Autowired lateinit var cartItemRepository: CartItemRepository

    private lateinit var listing1: ItemListing
    private lateinit var listing2: ItemListing

    private val now = OffsetDateTime.parse("2026-01-01T00:00:00Z")

    @BeforeEach
    fun init() {
        val listings = super.initListings()

        listing1 = listings[0]
        listing2 = listings[1]
    }

    /**
     * Initializes cart item data.
     *
     * @param guestSessionId Guest Session ID to associate the cart items under
     * @param daysOld Positive number to set back the timestamps of when the items were made
     * @param addBoth If both listings should be added (Default: true)
     */
    private fun addCartItems(
        guestSessionId: UUID,
        daysOld: Long,
        addBoth: Boolean = true,
    ) {
        val updatedAt = now.minusDays(daysOld)

        val item1 =
            CartItem(
                guestSessionId = guestSessionId,
                listingId = listing1.id!!,
                createdAt = updatedAt.minusDays(1),
                updatedAt = updatedAt,
            )

        cartItemRepository.save(item1)

        if (addBoth) {
            val item2 =
                CartItem(
                    guestSessionId = guestSessionId,
                    listingId = listing2.id!!,
                    createdAt = updatedAt.minusDays(1),
                    updatedAt = updatedAt,
                )

            cartItemRepository.save(item2)
        }
    }

    @Test
    fun `should cleanup cart items with cutoff days set to 3`() {
        val guest1Id = UUID.randomUUID()
        val guest2Id = UUID.randomUUID()

        // Initialize cart items
        addCartItems(guestSessionId = guest1Id, daysOld = 3)
        addCartItems(guestSessionId = guest2Id, daysOld = 2, addBoth = false)

        val cutoff = now.minusDays(3)

        val items = cartItemRepository.findCartItemsToClean(cutoff)

        items shouldHaveSize 2
        items.forAll { it.guestSessionId shouldBe guest1Id }
    }

    @Test
    fun `should cleanup cart items with cutoff days set to 2`() {
        val guest1Id = UUID.randomUUID()
        val guest2Id = UUID.randomUUID()

        // Initialize cart items
        addCartItems(guestSessionId = guest1Id, daysOld = 3)
        addCartItems(guestSessionId = guest2Id, daysOld = 2, addBoth = false)

        val cutoff = now.minusDays(2)

        val items = cartItemRepository.findCartItemsToClean(cutoff)

        items shouldHaveSize 3
    }

    @Test
    fun `should not cleanup any cart items when nothing meets cutoff`() {
        val guest1Id = UUID.randomUUID()
        val guest2Id = UUID.randomUUID()

        // Initialize cart items
        addCartItems(guestSessionId = guest1Id, daysOld = 3)
        addCartItems(guestSessionId = guest2Id, daysOld = 2, addBoth = false)

        // 5 day cutoff - nothing should be cleaned
        val cutoff = now.minusDays(5)

        val items = cartItemRepository.findCartItemsToClean(cutoff)

        items.shouldBeEmpty()
    }
}
