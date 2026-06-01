package com.yellrecords.services.user

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.user.dto.UpdateEmailRequest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : BaseH2Test() {
    companion object {
        const val BASE_PATH = "/api/users"
    }

    @Nested
    inner class GetUsers {
        @Test
        fun `should return admin account details`() {
            mockRequest(
                requestType = GET,
                path = "$BASE_PATH/${TestUsers.admin.id!!}",
                accessToken = TestAccessTokens.admin,
            ).andExpect(status().isOk)
        }

        @Test
        fun `should return 401 unauthorized when retrieving user information as non-user`() {
            mockRequest(
                requestType = GET,
                path = "$BASE_PATH/${TestUsers.admin.id!!}",
                accessToken = null,
            ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `should retrieve current user`() {
            mockRequest(
                requestType = GET,
                path = "$BASE_PATH/me",
                accessToken = TestAccessTokens.admin,
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.username").value(TestUsers.admin.username))
        }
    }

    @Nested
    inner class UpdateUser {
        @Test
        fun `should return 401 unauthorized on mismatched token for updating email`() {
            val req = UpdateEmailRequest(newEmail = "testbro@bademail.com")

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${TestUsers.admin.id!!}/email",
                accessToken = null,
                body = req,
            ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `should update user email`() {
            val req = UpdateEmailRequest(newEmail = "awesome@test.com")

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${TestUsers.admin.id!!}/email",
                accessToken = TestAccessTokens.admin,
                body = req,
            ).andExpect(status().isOk)

            val admin = userRepository.findById(TestUsers.admin.id!!).get()
            admin.email shouldBe req.newEmail
        }
    }
}
