package com.yellrecords.services.auth

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.auth.dto.ChangePasswordRequest
import com.yellrecords.services.auth.dto.LoginRequest
import com.yellrecords.services.user.dto.UserDto
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.readValue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class AuthControllerTest : BaseH2Test() {
    companion object {
        const val BASE_PATH = "/api/auth"

        const val ADMIN_CORRECT_PASSWORD = "admin"
    }

    @Nested
    inner class Login {
        @Test
        fun `should login user`() {
            val loginReq =
                LoginRequest(
                    username = TestUsers.admin.username,
                    rawPassword = ADMIN_CORRECT_PASSWORD,
                )

            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/login",
                body = loginReq,
                accessToken = null,
            ).andExpect(status().isOk)
        }

        @Test
        fun `should attach access and refresh cookies on login`() {
            val loginReq =
                LoginRequest(
                    username = TestUsers.admin.username,
                    rawPassword = ADMIN_CORRECT_PASSWORD,
                )

            val result =
                mockRequest(
                    requestType = POST,
                    path = "$BASE_PATH/login",
                    body = loginReq,
                    accessToken = null,
                ).andExpect(status().isOk)
                    .andReturn()

            val cookies = result.response.cookies
            cookies shouldHaveSize 2

            val accessCookie =
                cookies.firstOrNull { it.name == AuthService.ACCESS_TOKEN_NAME }.shouldNotBeNull()

            val refreshCookie =
                cookies.firstOrNull { it.name == AuthService.REFRESH_TOKEN_NAME }.shouldNotBeNull()

            accessCookie.maxAge shouldBe 15.minutes.inWholeSeconds
            accessCookie.isHttpOnly shouldBe true
            accessCookie.path shouldBe "/"

            refreshCookie.maxAge shouldBe 30.days.inWholeSeconds
            refreshCookie.isHttpOnly shouldBe true
            refreshCookie.path shouldBe "/api/auth/refresh"
        }

        @Test
        fun `non-existing user should return 400 bad request`() {
            val loginReq = LoginRequest(username = "1d9ub3fhu9", rawPassword = "123456")

            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/login",
                body = loginReq,
                accessToken = null,
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `invalid password should return 400 bad request`() {
            val loginReq =
                LoginRequest(username = TestUsers.admin.username, rawPassword = "1234561427")

            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/login",
                body = loginReq,
                accessToken = null,
            ).andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class ChangePassword {
        @Test
        fun `should return 401 unauthorized on mismatched token when changing password`() {
            val req =
                ChangePasswordRequest(
                    rawCurrent = ADMIN_CORRECT_PASSWORD,
                    rawNew = "someOtherPassword",
                    rawNew2 = "someOtherPassword",
                )

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/user/${TestUsers.admin.id}/change-password",
                accessToken = null,
                body = req,
            ).andExpect(status().isUnauthorized)
        }

        @Nested
        inner class InvalidateRequest {
            @Test
            fun `should return 400 bad request on incorrect current password`() {
                val req =
                    ChangePasswordRequest(
                        rawCurrent = "incorrect password",
                        rawNew = "someOtherPassword",
                        rawNew2 = "someOtherPassword",
                    )

                mockRequest(
                    requestType = PATCH,
                    path = "$BASE_PATH/user/${TestUsers.admin.id}/change-password",
                    accessToken = TestAccessTokens.admin,
                    body = req,
                ).andExpect(status().isBadRequest)
            }

            @Test
            fun `should return 400 bad request on non-matching new password`() {
                val req =
                    ChangePasswordRequest(
                        rawCurrent = ADMIN_CORRECT_PASSWORD,
                        rawNew = "someOtherPassword",
                        rawNew2 = "someOtherpassword",
                    )

                mockRequest(
                    requestType = PATCH,
                    path = "$BASE_PATH/user/${TestUsers.admin.id}/change-password",
                    accessToken = TestAccessTokens.admin,
                    body = req,
                ).andExpect(status().isBadRequest)
            }

            @Test
            fun `should return 400 bad request when new password matches current password`() {
                val req =
                    ChangePasswordRequest(
                        rawCurrent = ADMIN_CORRECT_PASSWORD,
                        rawNew = ADMIN_CORRECT_PASSWORD,
                        rawNew2 = ADMIN_CORRECT_PASSWORD,
                    )

                mockRequest(
                    requestType = PATCH,
                    path = "$BASE_PATH/user/${TestUsers.admin.id}/change-password",
                    accessToken = TestAccessTokens.admin,
                    body = req,
                ).andExpect(status().isBadRequest)
            }
        }

        @Test
        fun `should change password`() {
            val req =
                ChangePasswordRequest(
                    rawCurrent = ADMIN_CORRECT_PASSWORD,
                    rawNew = "someOtherPassword",
                    rawNew2 = "someOtherPassword",
                )

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/user/${TestUsers.admin.id}/change-password",
                accessToken = TestAccessTokens.admin,
                body = req,
            ).andExpect(status().isOk)

            val admin = userRepository.findById(TestUsers.admin.id!!).get()
            val passwordMatches = passwordEncoder.matches(req.rawNew, admin.passwordHash)
            passwordMatches shouldBe true
        }
    }

    @Nested
    inner class GetMe {
        @Test
        fun `should return 401 unauthorized on unauthenticated request`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/me", accessToken = null)
                .andExpect(status().isUnauthorized)
        }

        @Test
        fun `should get current authenticated user details`() {
            val result =
                mockRequest(
                    requestType = GET,
                    path = "$BASE_PATH/me",
                    accessToken = TestAccessTokens.admin,
                ).andExpect(status().isOk)
                    .andReturn()
                    .response
                    .contentAsString

            val user = objectMapper.readValue<UserDto>(result)
            user.id shouldBe TestUsers.admin.id
        }
    }

    @Nested
    inner class RefreshSession {
        @Test
        fun `should refresh admin session`() {
            val result =
                mockRequest(
                    requestType = POST,
                    path = "$BASE_PATH/refresh",
                    accessToken = null,
                    refreshToken = TestRefreshTokens.admin,
                ).andExpect(status().isOk)
                    .andReturn()

            val cookies = result.response.cookies
            cookies shouldHaveSize 2

            val accessCookie =
                cookies.firstOrNull { it.name == AuthService.ACCESS_TOKEN_NAME }.shouldNotBeNull()

            accessCookie.maxAge shouldBe 15.minutes.inWholeSeconds
            accessCookie.isHttpOnly shouldBe true
            accessCookie.path shouldBe "/"
        }

        @Test
        fun `should return 401 unauthorized with 0 cookie tokens`() {
            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/refresh",
                accessToken = null,
                refreshToken = null,
            ).andExpect(status().isUnauthorized)
        }
    }

    @Nested
    inner class Logout {
        @Test
        fun `should remove both access and refresh token cookies`() {
            val result =
                mockRequest(
                    requestType = POST,
                    path = "$BASE_PATH/logout",
                    accessToken = TestAccessTokens.admin,
                    refreshToken = TestRefreshTokens.admin,
                ).andExpect(status().isOk)
                    .andReturn()

            val cookies = result.response.cookies
            cookies shouldHaveSize 2

            val accessCookie =
                cookies.firstOrNull { it.name == AuthService.ACCESS_TOKEN_NAME }.shouldNotBeNull()
            val refreshCookie =
                cookies.firstOrNull { it.name == AuthService.REFRESH_TOKEN_NAME }.shouldNotBeNull()

            accessCookie.value.shouldBeEmpty()
            accessCookie.maxAge shouldBe 0

            refreshCookie.value.shouldBeEmpty()
            refreshCookie.maxAge shouldBe 0
        }
    }
}
