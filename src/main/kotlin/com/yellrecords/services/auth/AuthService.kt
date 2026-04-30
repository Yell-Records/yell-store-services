package com.yellrecords.services.auth

import com.yellrecords.services.auth.dto.LoginResponse
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

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
}
