package com.yellrecords.services.order

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.cart.CartItem
import com.yellrecords.services.cart.CartItemRepository
import com.yellrecords.services.cart.CartItemService
import com.yellrecords.services.itemlisting.ItemListing
import com.yellrecords.services.order.dto.CreateOrderRequestDto
import com.yellrecords.services.order.dto.OrderDto
import com.yellrecords.services.order.dto.TrackingDetailsDto
import com.yellrecords.services.order.dto.UpdateOrderDto
import com.yellrecords.services.orderitem.OrderItemRepository
import com.yellrecords.services.paypal.PayPalClient
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.readValue
import java.math.BigDecimal
import java.util.UUID

class OrderControllerTest : BaseH2Test() {
    companion object {
        private const val BASE_PATH = "/api/orders"
        private const val MOCK_PAYPAL_ORDER_ID = "5O190127TN364715T"
        private const val MOCK_PAYPAL_CAPTURE_ID = "7JD73392FH834931L"
    }

    @Autowired lateinit var cartItemService: CartItemService

    @Autowired lateinit var orderRepository: OrderRepository

    @Autowired lateinit var cartItemRepository: CartItemRepository

    @Autowired lateinit var orderItemRepository: OrderItemRepository

    @Autowired lateinit var orderService: OrderService

    private lateinit var listing1: ItemListing
    private lateinit var listing2: ItemListing

    private val guestId = UUID.randomUUID()

    private val mockPayPalClient = mock<PayPalClient>()

    /** Pre-fills a create order request with shipping information. */
    private fun genCreateOrder(
        guestSessionId: UUID = guestId,
        buyerEmail: String = "test@email.com",
        totalPaid: BigDecimal = BigDecimal("1.00"),
    ) = CreateOrderRequestDto(
        guestSessionId = guestSessionId,
        buyerEmail = buyerEmail,
        subtotal = totalPaid,
        shippingFirstName = "Test",
        shippingLastName = "Last",
        shippingAddressLine1 = "123 street",
        shippingAddressLine2 = null,
        shippingCity = "New York",
        shippingState = "New York",
        shippingPostalCode = "55555",
        shippingPhone = "555-555-5555",
        acceptedTerms = true,
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

        @Test
        fun `should create order items based on items in cart`() {
            val guestRequest = genCreateOrder(totalPaid = BigDecimal("300.0"))

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = guestRequest)
                .andExpect(status().isCreated)

            val allOrders = orderRepository.findAll().toList()
            allOrders shouldHaveSize 1

            val guestOrder = allOrders.first()
            val orderItems = orderItemRepository.findOrderItemsByOrderId(guestOrder.id!!)
            orderItems shouldHaveSize 1
            orderItems.forOne { it.listingId shouldBe listing1.id }
        }

        @Test
        fun `should clear cart items after payment capture`() {
            val guestRequest =
                genCreateOrder(guestSessionId = guestId, buyerEmail = "email@test.com")

            val dto = orderService.createOrder(guestRequest)

            val saved = orderRepository.findById(dto.id).get()
            saved.paypalOrderId = MOCK_PAYPAL_ORDER_ID

            whenever(mockPayPalClient.captureOrder(saved.paypalOrderId!!))
                .thenReturn(MOCK_PAYPAL_CAPTURE_ID)

            val preCartItems = cartItemRepository.findGuestCartItems(guestId)
            preCartItems.shouldNotBeEmpty()

            val mockOrderService =
                OrderService(
                    orderRepository = orderRepository,
                    cartItemService = cartItemService,
                    paypalClient = mockPayPalClient,
                )

            mockOrderService.captureOrder(saved.id!!)

            // Check cart
            val cartItems = cartItemRepository.findGuestCartItems(guestId)
            cartItems.shouldBeEmpty()
        }
    }

    @Nested
    inner class GetOrders {
        @BeforeEach
        fun init() {
            val guestRequest =
                genCreateOrder(guestSessionId = guestId, buyerEmail = "email@test.com")

            val dto = orderService.createOrder(guestRequest)

            // Set status to paid to simulate in-progress
            val saved = orderRepository.findById(dto.id).get()
            saved.status = OrderStatus.PAID

            orderRepository.save(saved)
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

            orders.first().status shouldBe OrderStatus.PAID
        }

        @Test
        fun `should get finished orders`() {
            // Create a new completed order
            val guestRequest =
                genCreateOrder(guestSessionId = guestId, buyerEmail = "email@test.com")

            val dto = orderService.createOrder(guestRequest)

            // Set status to paid to simulate in-progress
            val saved = orderRepository.findById(dto.id).get()
            saved.status = OrderStatus.SHIPPED

            orderRepository.save(saved)

            val result =
                mockRequest(
                    requestType = GET,
                    path = BASE_PATH,
                    token = TestTokens.admin,
                    params = mapOf("unfinished" to "false"),
                ).andExpect(status().isOk)
                    .andReturn()

            val body = result.response.contentAsString
            val orders = objectMapper.readValue<List<OrderDto>>(body)
            orders shouldHaveSize 1

            orders.first().status shouldBe OrderStatus.SHIPPED
        }

        @Test
        fun `should get order by id`() {
            val sampleOrder = orderRepository.findAll().first()

            val result =
                mockRequest(
                    requestType = GET,
                    path = "$BASE_PATH/${sampleOrder.id}",
                    token = TestTokens.admin,
                ).andExpect(status().isOk)
                    .andReturn()

            val body = result.response.contentAsString
            val order = objectMapper.readValue<OrderDto>(body)

            order.id shouldBe sampleOrder.id
        }
    }

    @Nested
    inner class PatchOrder {
        private lateinit var guestOrder: Order

        @BeforeEach
        fun init() {
            val guestRequest =
                genCreateOrder(guestSessionId = guestId, buyerEmail = "email@test.com")

            val dto = orderService.createOrder(guestRequest)

            guestOrder = orderRepository.findById(dto.id).get()
        }

        @Test
        fun `should return 403 forbidden on mis-match guest session id`() {
            val req = UpdateOrderDto(guestSessionId = UUID.randomUUID(), shippingCity = "NY")

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${guestOrder.id}",
                token = null,
                body = req,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return 403 forbidden when status has changed`() {
            guestOrder.status = OrderStatus.PAID

            orderRepository.save(guestOrder)

            val req = UpdateOrderDto(guestSessionId = guestId, shippingCity = "NY")

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${guestOrder.id}",
                token = null,
                body = req,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should update order details`() {
            val req =
                UpdateOrderDto(
                    guestSessionId = guestId,
                    shippingCity = "NY",
                    shippingFirstName = "Toby",
                )

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${guestOrder.id}",
                token = null,
                body = req,
            ).andExpect(status().isOk)

            val updatedOrder = orderRepository.findById(guestOrder.id!!).get()
            updatedOrder.shippingCity shouldBe req.shippingCity
            updatedOrder.shippingFirstName shouldBe req.shippingFirstName
        }

        @Nested
        inner class StatusChanges {
            @BeforeEach
            fun init() {
                val guestRequest =
                    genCreateOrder(guestSessionId = guestId, buyerEmail = "email@test.com")

                val dto = orderService.createOrder(guestRequest)

                // Set status to paid to simulate in-progress
                guestOrder = orderRepository.findById(dto.id).get()
                guestOrder.status = OrderStatus.PAID
                orderRepository.save(guestOrder)
            }

            @Nested
            inner class ForbiddenTests {
                @Test
                fun `confirm order should return 403 forbidden when not authenticated`() {
                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/confirm",
                        token = null,
                    ).andExpect(status().isForbidden)
                }

                @Test
                fun `ship order should return 403 forbidden when not authenticated`() {
                    val req = TrackingDetailsDto(trackingNumber = "1Z999999999999")

                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/shipped",
                        token = null,
                        body = req,
                    ).andExpect(status().isForbidden)
                }

                @Test
                fun `fulfill order should return 403 forbidden when not authenticated`() {
                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/fulfill",
                        token = null,
                    ).andExpect(status().isForbidden)
                }

                @Test
                fun `cancel order should return 403 forbidden when not authenticated`() {
                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/cancel",
                        token = null,
                    ).andExpect(status().isForbidden)
                }
            }

            @Nested
            inner class ConfirmOrder {
                @Test
                fun `should return 409 conflict when order status is not paid`() {
                    guestOrder.status = OrderStatus.IN_PROGRESS

                    orderRepository.save(guestOrder)

                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/confirm",
                        token = TestTokens.admin,
                    ).andExpect(status().isConflict)
                }

                @Test
                fun `should change status to in progress`() {
                    guestOrder.status shouldBe OrderStatus.PAID

                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/confirm",
                        token = TestTokens.admin,
                    ).andExpect(status().isOk)

                    val updatedOrder = orderRepository.findById(guestOrder.id!!).get()
                    updatedOrder.status shouldBe OrderStatus.IN_PROGRESS
                }
            }

            @Nested
            inner class ShipOrder {
                private val sampleReq = TrackingDetailsDto(trackingNumber = "1Z9999999999999999")

                @Test
                fun `should return 409 conflict when status is not in progress`() {
                    guestOrder.status shouldNotBe OrderStatus.IN_PROGRESS

                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/shipped",
                        token = TestTokens.admin,
                        body = sampleReq,
                    ).andExpect(status().isConflict)
                }

                @Test
                fun `should update status to shipped and set required fields`() {
                    guestOrder.status = OrderStatus.IN_PROGRESS
                    orderRepository.save(guestOrder)

                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/shipped",
                        token = TestTokens.admin,
                        body = sampleReq,
                    ).andExpect(status().isOk)

                    val updatedOrder = orderRepository.findById(guestOrder.id!!).get()
                    updatedOrder.status shouldBe OrderStatus.SHIPPED
                    updatedOrder.trackingNumber shouldBe sampleReq.trackingNumber
                    updatedOrder.shippedAt.shouldNotBeNull()
                }
            }

            @Nested
            inner class FulfillOrder {
                @Test
                fun `should return 409 conflict when status is not shipped`() {
                    guestOrder.status shouldNotBe OrderStatus.SHIPPED

                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/fulfill",
                        token = TestTokens.admin,
                    ).andExpect(status().isConflict)
                }

                @Test
                fun `should set status to fulfilled`() {
                    guestOrder.status = OrderStatus.SHIPPED

                    orderRepository.save(guestOrder)

                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/fulfill",
                        token = TestTokens.admin,
                    ).andExpect(status().isOk)

                    val updatedOrder = orderRepository.findById(guestOrder.id!!).get()

                    updatedOrder.status shouldBe OrderStatus.FULFILLED
                }
            }

            @Nested
            inner class CancelOrder {
                @Test
                fun `should return 409 conflict when status has already indicated shipped`() {
                    val shippedStatuses = setOf(OrderStatus.SHIPPED, OrderStatus.FULFILLED)

                    shippedStatuses.forEach { status ->
                        guestOrder.status = status
                        orderRepository.save(guestOrder)

                        mockRequest(
                            requestType = PATCH,
                            path = "$BASE_PATH/${guestOrder.id}/cancel",
                            token = TestTokens.admin,
                        ).andExpect(status().isConflict)
                    }
                }

                @Test
                fun `should cancel order if not in shipped state`() {
                    val preShippedStatuses =
                        setOf(
                            OrderStatus.AWAITING_PAYMENT,
                            OrderStatus.PAID,
                            OrderStatus.IN_PROGRESS,
                        )

                    preShippedStatuses.forEach { status ->
                        guestOrder.status = status
                        orderRepository.save(guestOrder)

                        mockRequest(
                            requestType = PATCH,
                            path = "$BASE_PATH/${guestOrder.id}/cancel",
                            token = TestTokens.admin,
                        ).andExpect(status().isOk)

                        val updatedOrder = orderRepository.findById(guestOrder.id!!).get()

                        updatedOrder.status shouldBe OrderStatus.CANCELED
                    }
                }
            }
        }

        @Nested
        inner class AnonymizeOrder {
            @Test
            fun `should return 403 forbidden when anonymizing without token`() {
                mockRequest(
                    requestType = PATCH,
                    path = "$BASE_PATH/${guestOrder.id}/anonymize",
                    token = null,
                ).andExpect(status().isForbidden)
            }

            @Test
            fun `should return 409 conflict when order state is pre-shipped`() {
                guestOrder.status = OrderStatus.IN_PROGRESS

                orderRepository.save(guestOrder)

                mockRequest(
                    requestType = PATCH,
                    path = "$BASE_PATH/${guestOrder.id}/anonymize",
                    token = TestTokens.admin,
                ).andExpect(status().isConflict)
            }

            @Test
            fun `should anonymize order`() {
                guestOrder.status = OrderStatus.FULFILLED

                orderRepository.save(guestOrder)

                val result =
                    mockRequest(
                        requestType = PATCH,
                        path = "$BASE_PATH/${guestOrder.id}/anonymize",
                        token = TestTokens.admin,
                    ).andExpect(status().isOk)
                        .andReturn()

                val body = result.response.contentAsString
                val order = objectMapper.readValue<OrderDto>(body)

                order.shippingFirstname shouldBe "Deleted"
                order.shippingLastname shouldBe "User"
                order.shippingAddressLine1 shouldBe "Anonymized"
                order.shippingAddressLine2 shouldBe null
                order.buyerEmail shouldBe "deleted+${guestOrder.orderNumber}@example.com"
                order.shippingCity shouldBe "Anonymized"
                order.shippingPhone shouldBe "0000000000"
                order.shippingPostalCode shouldBe "00000"
                order.anonymized shouldBe true
                order.anonymizedAt.shouldNotBeNull()

                // Should not anonymize shipping state
                order.shippingState shouldBe guestOrder.shippingState

                // Test for no content
                mockRequest(
                    requestType = PATCH,
                    path = "$BASE_PATH/${guestOrder.id}/anonymize",
                    token = TestTokens.admin,
                ).andExpect(status().isNoContent)
            }
        }
    }
}
