package com.yellrecords.services.auth

import com.yellrecords.services.auth.dto.ChangePasswordRequest
import com.yellrecords.services.auth.dto.LoginRequest
import com.yellrecords.services.user.dto.UserDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody loginReq: LoginRequest,
        response: HttpServletResponse,
    ): ResponseEntity<UserDto> = authService.login(loginReq, response)

    @GetMapping("/me")
    fun getAuthenticated(): ResponseEntity<UserDto> = authService.currentAuthUser()

    @PostMapping("/refresh")
    fun refreshSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Void> = authService.refreshSession(request, response)

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> = authService.logoutUser(response)

    @PatchMapping("/user/{id}/change-password")
    @PreAuthorize("isAuthenticated() && #id == authentication.principal.id")
    fun changePassword(
        @PathVariable id: UUID,
        @RequestBody changeReq: ChangePasswordRequest,
    ): ResponseEntity<Void> = authService.changePassword(id, changeReq)
}
