package com.yellrecords.services.itemlisting

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.category.Category
import com.yellrecords.services.itemlisting.dto.CreateListingRequest
import com.yellrecords.services.itemlisting.dto.ItemListingDto
import com.yellrecords.services.itemlisting.dto.UpdateListingRequest
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.readValue
import java.math.BigDecimal
import java.util.UUID

class ItemListingControllerTest : BaseH2Test() {
    companion object {
        const val BASE_PATH = "/api/item-listings"
    }

    private lateinit var listing1: ItemListing
    private lateinit var listing2: ItemListing

    @BeforeEach
    fun initializeListings() {
        val listings = super.initListings()
        listing1 = listings.first()
        listing2 = listings.last()
    }

    @Nested
    inner class CreateItemListing {
        @Test
        fun `item listing created by non-user should return 403 forbidden`() {
            val category =
                categoryRepository.findCategoryBySlug("sample-category").shouldNotBeNull()

            val req =
                CreateListingRequest(
                    title = "Test",
                    description = "Test",
                    imageUrl = null,
                    price = BigDecimal("10.00"),
                    categorySlug = category.slug,
                )

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = req)
                .andExpect(status().isForbidden)
        }

        @Test
        fun `should create item listing`() {
            val category =
                categoryRepository.findCategoryBySlug("sample-category").shouldNotBeNull()

            val req =
                CreateListingRequest(
                    title = "Test 2",
                    description = "Test",
                    imageUrl = null,
                    price = BigDecimal("10.00"),
                    categorySlug = category.slug,
                )

            mockRequest(requestType = POST, path = BASE_PATH, token = TestTokens.admin, body = req)
                .andExpect(status().isCreated)
        }
    }

    @Nested
    inner class GetItemListing {
        @Test
        fun `should get all item listings`() {
            mockRequest(requestType = GET, path = BASE_PATH, token = null)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
        }

        @Test
        fun `should get single item listing by id`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/listing/${listing1.id}", token = null)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(listing1.id.toString()))
        }

        @Test
        fun `should return 404 not found on unknown listing id`() {
            mockRequest(
                requestType = GET,
                path = "$BASE_PATH/listing/${UUID.randomUUID()}",
                token = null,
            ).andExpect(status().isNotFound)
        }

        @Nested
        inner class GetByCategory {
            private lateinit var sampleCategory: Category
            private lateinit var otherCategory: Category

            private lateinit var otherListing: ItemListing

            @BeforeEach
            fun initCategoryListings() {
                sampleCategory = categoryRepository.findCategoryBySlug("sample-category")!!
                otherCategory =
                    categoryRepository.save(
                        Category(name = "Test Category", slug = "test-category"),
                    )

                otherListing =
                    itemListingRepository.save(
                        ItemListing(
                            title = "New Listing",
                            description = "New listing.",
                            price = BigDecimal.valueOf(5000),
                            imageUrl = null,
                            categoryId = otherCategory.id!!,
                        ),
                    )
            }

            @Test
            fun `should get listings by category`() {
                val result =
                    mockRequest(
                        requestType = GET,
                        path = "$BASE_PATH/category/${sampleCategory.slug}",
                        token = null,
                    ).andExpect(status().isOk)
                        .andReturn()

                val body = result.response.contentAsString
                val listings = objectMapper.readValue<List<ItemListingDto>>(body)

                listings.shouldNotBeEmpty()
                listings.forAll { it.categorySlug shouldBe sampleCategory.slug }
            }

            @Test
            fun `should return 404 not found when retrieving listings from inactive category`() {
                sampleCategory.isActive = false

                mockRequest(
                    requestType = GET,
                    path = "$BASE_PATH/category/${sampleCategory.slug}",
                    token = null,
                ).andExpect(status().isNotFound)
            }
        }
    }

    @Nested
    inner class UpdateItemListing {
        @Test
        fun `should update item listing`() {
            val otherCategory =
                categoryRepository.save(Category(name = "New Category", slug = "new-category"))

            val req =
                UpdateListingRequest(
                    title = "new title",
                    description = "new description",
                    price = BigDecimal.valueOf(5000),
                    categorySlug = otherCategory.slug,
                )

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${listing1.id}",
                token = TestTokens.admin,
                body = req,
            ).andExpect(status().isOk)

            val listing = itemListingRepository.findById(listing1.id!!).get()

            listing.title shouldBe req.title
            listing.description shouldBe req.description
            listing.price shouldBe req.price
            listing.categoryId shouldBe otherCategory.id!!
        }

        @Test
        fun `should return 403 forbidden on unknown user changing listing details`() {
            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${listing1.id}",
                token = null,
                body = UpdateListingRequest(price = BigDecimal(0)),
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return 400 bad request when setting price to below 0`() {
            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${listing1.id}",
                token = TestTokens.admin,
                body = UpdateListingRequest(price = BigDecimal(-1)),
            ).andExpect(status().isBadRequest)
        }
    }
}
