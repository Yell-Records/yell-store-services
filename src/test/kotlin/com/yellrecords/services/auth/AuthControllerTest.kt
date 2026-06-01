package com.yellrecords.services.auth

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.auth.dto.ChangePasswordRequest
import com.yellrecords.services.auth.dto.LoginRequest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
                token = null,
            ).andExpect(status().isOk)
        }

        @Test
        fun `non-existing user should return 400 bad request`() {
            val loginReq = LoginRequest(username = "1d9ub3fhu9", rawPassword = "123456")

            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/login",
                body = loginReq,
                token = null,
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
                token = null,
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
                token = null,
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
                    token = TestTokens.admin,
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
                    token = TestTokens.admin,
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
                    token = TestTokens.admin,
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
                token = TestTokens.admin,
                body = req,
            ).andExpect(status().isOk)

            val admin = userRepository.findById(TestUsers.admin.id!!).get()
            val passwordMatches = passwordEncoder.matches(req.rawNew, admin.passwordHash)
            passwordMatches shouldBe true
        }
    }
}
