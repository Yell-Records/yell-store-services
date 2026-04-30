package com.yellrecords.services.order

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.cart.CartItem
import com.yellrecords.services.cart.CartItemRepository
import com.yellrecords.services.itemlisting.ItemListing
import com.yellrecords.services.order.dto.CreateOrderRequestDto
import com.yellrecords.services.order.dto.OrderDto
import com.yellrecords.services.orderitem.OrderItemRepository
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.readValue
import java.math.BigDecimal
import java.util.UUID

class OrderControllerTest : BaseH2Test() {
    companion object {
        private const val BASE_PATH = "/api/orders"
    }

    @Autowired lateinit var orderRepository: OrderRepository

    @Autowired lateinit var cartItemRepository: CartItemRepository

    @Autowired lateinit var orderItemRepository: OrderItemRepository

    @Autowired lateinit var orderService: OrderService

    private lateinit var listing1: ItemListing
    private lateinit var listing2: ItemListing

    private val guestId = UUID.randomUUID()

    /** Pre-fills a create order request with shipping information. */
    private fun genCreateOrder(
        guestSessionId: UUID = guestId,
        buyerEmail: String = "test@email.com",
        totalPaid: BigDecimal = BigDecimal("1.00"),
    ) = CreateOrderRequestDto(
        guestSessionId = guestSessionId,
        buyerEmail = buyerEmail,
        totalPaid = totalPaid,
        shippingFirstName = "Test",
        shippingLastName = "Last",
        shippingAddressLine1 = "123 street",
        shippingAddressLine2 = null,
        shippingCity = "New York",
        shippingState = "New York",
        shippingPostalCode = "55555",
        shippingPhone = "555-555-5555",
    )

    @BeforeEach
    fun init() {
        val listings = super.initListings()

        listing1 = listings.first()
        listing2 = listings.last()

        // Guest cart item
        val guestCartItem =
            CartItem(guestSessionId = guestId, listingId = listing1.id!!, quantity = 1)

        cartItemRepository.save(guestCartItem)
    }

    @Nested
    inner class CreateOrders {
        @Test
        fun `should create order for non-user`() {
            val req = genCreateOrder()

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = req)
                .andExpect(status().isCreated)
        }

        @Test
        fun `should return 400 bad request on invalid total paid values`() {
            val invalidValues = listOf(BigDecimal("-1.0"), BigDecimal("-9999.0"), BigDecimal("0.0"))

            invalidValues.forEach { value ->
                val req = genCreateOrder(totalPaid = value)

                mockRequest(requestType = POST, path = BASE_PATH, token = null, body = req)
                    .andExpect(status().isBadRequest)
            }
        }

        @Nested
        inner class IntegrityTests {
            private val guestRequest = genCreateOrder(totalPaid = BigDecimal("300.0"))

            @Test
            fun `should clear buyer's cart items`() {
                val prevItems = cartItemRepository.findGuestCartItems(guestId)
                prevItems shouldHaveSize 2

                mockRequest(requestType = POST, path = BASE_PATH, token = null, body = guestRequest)
                    .andExpect(status().isCreated)

                val userItems = cartItemRepository.findGuestCartItems(guestId)
                userItems shouldHaveSize 0
            }

            @Test
            fun `should create order items based on items in cart`() {
                mockRequest(
                    requestType = POST,
                    path = BASE_PATH,
                    token = TestTokens.admin,
                    body = guestRequest,
                ).andExpect(status().isCreated)

                val userOrders = orderRepository.findAll().toList()
                userOrders shouldHaveSize 1

                val userOrder = userOrders.first()
                val orderItems = orderItemRepository.findOrderItemsByOrderId(userOrder.id!!)
                orderItems shouldHaveSize 2
                orderItems.forOne { it.listingId shouldBe listing1.id }
                orderItems.forOne { it.listingId shouldBe listing2.id }
            }
        }
    }

    @Nested
    inner class GetOrders {
        @BeforeEach
        fun init() {
            val guestRequest =
                genCreateOrder(guestSessionId = guestId, buyerEmail = "email@test.com")

            orderService.createOrder(guestRequest)
        }

        @Test
        fun `should get unfinished orders`() {
            val result =
                mockRequest(
                    requestType = GET,
                    path = BASE_PATH,
                    token = TestTokens.admin,
                    params = mapOf("unfinished" to "true"),
                ).andExpect(status().isOk)
                    .andReturn()

            val body = result.response.contentAsString
            val orders = objectMapper.readValue<List<OrderDto>>(body)
            orders shouldHaveSize 1

            orders.first().status shouldBe OrderStatus.IN_PROGRESS
        }
    }
}
