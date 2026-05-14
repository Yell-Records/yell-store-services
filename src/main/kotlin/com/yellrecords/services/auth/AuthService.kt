package com.yellrecords.services.auth

import com.yellrecords.services.auth.dto.ChangePasswordRequest
import com.yellrecords.services.auth.dto.LoginResponse
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.exception.NotFoundException
import com.yellrecords.services.user.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
) {
    /**
     * Validates the provided credentials against a user in the database, then creates a new Java
     * Web Token for the user.
     *
     * @throws BadRequestException If the username does not exist OR the password does not match.
     */
    fun login(
        username: String,
        password: String,
    ): LoginResponse {
        val user =
            userRepository.findByUsernameIgnoreCase(username)
                ?: throw BadRequestException("Invalid credentials.")

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw BadRequestException("Invalid credentials.")
        }

        val token = jwtService.generateToken(user.username, user.id!!, user.role)

        return LoginResponse(token = token, username = username)
    }

    @Transactional
    fun changePassword(
        userId: UUID,
        changeRequest: ChangePasswordRequest,
    ): ResponseEntity<Void> {
        val user =
            userRepository.findById(userId).getOrElse { throw NotFoundException("User not found.") }

        // Validate current password
        if (!passwordEncoder.matches(changeRequest.rawCurrent, user.passwordHash)) {
            throw BadRequestException("Current password does not match.")
        }

        // Ensure equality on confirm password
        if (changeRequest.rawNew != changeRequest.rawNew2) {
            throw BadRequestException("New password does not match.")
        }

        // Ensure new password is different
        if (changeRequest.rawCurrent == changeRequest.rawNew) {
            throw BadRequestException("New password must be different from current password.")
        }

        val hashedNew =
            passwordEncoder.encode(changeRequest.rawNew) ?: error("Could not hash password.")

        user.passwordHash = hashedNew

        return ResponseEntity.ok().build()
    }
}
