package com.yellrecords.services

import com.yellrecords.services.auth.JwtService
import com.yellrecords.services.category.Category
import com.yellrecords.services.category.CategoryRepository
import com.yellrecords.services.config.SecurityConfig
import com.yellrecords.services.itemlisting.ItemListing
import com.yellrecords.services.itemlisting.ItemListingRepository
import com.yellrecords.services.user.User
import com.yellrecords.services.user.UserRepository
import com.yellrecords.services.user.UserRole
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import kotlin.jvm.optionals.getOrNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Import(SecurityConfig::class)
abstract class BaseH2Test {
    @Autowired protected lateinit var objectMapper: ObjectMapper

    @Autowired protected lateinit var passwordEncoder: PasswordEncoder

    @Autowired protected lateinit var userRepository: UserRepository

    @Autowired protected lateinit var itemListingRepository: ItemListingRepository

    @Autowired protected lateinit var categoryRepository: CategoryRepository

    @Autowired protected lateinit var jwtService: JwtService

    @Autowired protected lateinit var mockMvc: MockMvc

    /**
     * A container for referencing preset registered users.
     *
     * @property admin Default user with admin permissions.
     */
    protected object TestUsers {
        lateinit var admin: User
    }

    /**
     * A container for holding preset Java Web Tokens.
     *
     * @property admin Client with admin-level privilege.
     */
    protected object TestTokens {
        lateinit var admin: String
    }

    @BeforeAll
    fun seedUsers() {
        val json = javaClass.getResourceAsStream("/data/users.json")
        val users = objectMapper.readValue(json, Array<TestUserSeed>::class.java)

        userRepository.deleteAll()

        users.forEach { seed ->
            val newUser =
                userRepository.save(
                    User(
                        username = seed.username,
                        passwordHash =
                            passwordEncoder.encode(seed.password)
                                ?: throw RuntimeException(
                                    "Cannot encode password: ${seed.password}",
                                ),
                        email = seed.email,
                        role = seed.role,
                    ),
                )

            val token = jwtService.generateToken(seed.username, newUser.id!!, newUser.role)

            when (seed.role.uppercase()) {
                UserRole.ADMIN -> {
                    TestUsers.admin = newUser
                    TestTokens.admin = token
                }

                else -> {
                    TestUsers.admin = newUser
                    TestTokens.admin = token
                }
            }
        }
    }

    /**
     * This method first creates a new category, then initializes the item listing repository with
     * two listings:
     * 1. $100 item
     * 2. $250 item
     *
     * @return All item listings.
     */
    protected fun initListings(): List<ItemListing> {
        val category =
            categoryRepository.save(Category(name = "Sample Category", slug = "sample-category"))

        itemListingRepository.save(
            ItemListing(
                title = "Test Listing 1",
                description = "Test listing.",
                price = BigDecimal.valueOf(100),
                categoryId = category.id!!,
            ),
        )

        itemListingRepository.save(
            ItemListing(
                title = "Test Listing 2",
                description = "Test listing, but admin.",
                price = BigDecimal.valueOf(250),
                categoryId = category.id!!,
            ),
        )

        return itemListingRepository.findAll()
    }

    protected fun ItemListing.category() = categoryRepository.findById(this.categoryId).getOrNull()

    /**
     * Sends a mock HTTP request to a specified rest controller.
     *
     * @param requestType Method type of the controller.
     * @param path URI of the controller.
     * @param token Which [TestTokens] to use for this call, or `null` if non-user.
     * @param body Data body in the request for `POST` calls.
     * @param params Parameters to add to the request.
     */
    protected fun mockRequest(
        requestType: HttpMethod,
        path: String,
        token: String?,
        body: Any? = null,
        params: Map<String, String> = emptyMap(),
    ): ResultActions {
        val builder =
            when (requestType) {
                HttpMethod.GET -> MockMvcRequestBuilders.get(path)
                HttpMethod.POST -> MockMvcRequestBuilders.post(path)
                HttpMethod.PUT -> MockMvcRequestBuilders.put(path)
                HttpMethod.DELETE -> MockMvcRequestBuilders.delete(path)
                HttpMethod.OPTIONS -> MockMvcRequestBuilders.options(path)
                HttpMethod.HEAD -> MockMvcRequestBuilders.head(path)
                HttpMethod.PATCH -> MockMvcRequestBuilders.patch(path)
                else -> throw RuntimeException("Unhandled request: $requestType")
            }

        if (body != null) {
            builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        }

        if (token != null) {
            builder.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }

        params.forEach { (k, v) -> builder.param(k, v) }

        return mockMvc.perform(builder)
    }
}
