package com.yellrecords.services.auth

import com.yellrecords.services.auth.dto.LoginRequest
import com.yellrecords.services.auth.dto.LoginResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody loginReq: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val res = authService.login(loginReq.username, loginReq.rawPassword)

        return ResponseEntity.ok(res)
    }
}
