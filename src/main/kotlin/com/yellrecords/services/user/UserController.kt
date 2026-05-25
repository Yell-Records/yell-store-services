package com.yellrecords.services.user

import com.yellrecords.services.auth.CustomUserDetails
import com.yellrecords.services.user.dto.UpdateEmailRequest
import com.yellrecords.services.user.dto.UserDto
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val service: UserService,
) {
    @GetMapping("/{id}")
    @RolesAllowed(UserRole.ADMIN)
    fun getUserById(
        @PathVariable id: UUID,
    ): UserDto = service.getUserById(id)

    @GetMapping("/me")
    @RolesAllowed(UserRole.ADMIN)
    fun getMyself(
        @AuthenticationPrincipal user: CustomUserDetails,
    ): UserDto = service.getUserById(user.id)

    @GetMapping("/admin")
    @RolesAllowed(UserRole.ADMIN)
    fun getAdmin(
        @PathVariable id: UUID,
    ): UserDto = service.findAdmin()

    @PatchMapping("/{id}/email")
    @PreAuthorize("isAuthenticated() && #id == authentication.principal.id")
    fun updateUserEmail(
        @PathVariable id: UUID,
        @RequestBody emailReq: UpdateEmailRequest,
    ): ResponseEntity<Void> = service.updateEmail(id, emailReq)
}
