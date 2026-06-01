package com.yellrecords.services.auth

import com.yellrecords.services.auth.dto.ChangePasswordRequest
import com.yellrecords.services.auth.dto.LoginRequest
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.exception.ForbiddenException
import com.yellrecords.services.exception.NotFoundException
import com.yellrecords.services.user.User
import com.yellrecords.services.user.UserRepository
import com.yellrecords.services.user.dto.UserDto
import com.yellrecords.services.user.mapper.UserMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: CustomUserDetailsService,
) {
    /**
     * Validates the provided credentials against a user in the database, then creates a new Java
     * Web Token for the user.
     *
     * @throws BadRequestException If the username does not exist OR the password does not match.
     */
    fun login(
        request: LoginRequest,
        response: HttpServletResponse,
    ): ResponseEntity<UserDto> {
        val user =
            userRepository.findByUsernameIgnoreCase(request.username)
                ?: throw BadRequestException("Invalid credentials.")

        if (!passwordEncoder.matches(request.rawPassword, user.passwordHash)) {
            throw BadRequestException("Invalid credentials.")
        }

        // Initialize the security headers
        val accessCookie = generateAccessCookie(user)
        val refreshCookie = generateRefreshCookie(user)

        response.addHeader("Set-Cookie", accessCookie.toString())
        response.addHeader("Set-Cookie", refreshCookie.toString())

        val userDto = UserMapper.toDto(user)

        return ResponseEntity(userDto, HttpStatus.OK)
    }

    /**
     * Retrieves the currently authenticated user details.
     *
     * If there is no authenticated user, throws a 401 status.
     */
    fun currentAuthUser(): ResponseEntity<UserDto> {
        val auth = SecurityContextHolder.getContext().authentication

        return if (auth != null && auth.isAuthenticated && auth.principal is CustomUserDetails) {
            val username = (auth.principal as CustomUserDetails).username
            val user =
                userRepository.findByUsernameIgnoreCase(username)
                    ?: throw NotFoundException(
                        "User not found when checking login status: $username",
                    )

            val userDto = UserMapper.toDto(user)

            ResponseEntity(userDto, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
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

    fun refreshSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        // Retrieve the refresh token
        val refreshToken =
            request.cookies?.firstOrNull { it.name == REFRESH_TOKEN_NAME }?.value
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        // Get username from the cookie
        val username =
            jwtService.extractUsername(refreshToken) ?: throw ForbiddenException("Invalid token.")

        // Load the user details
        val userDetails = userDetailsService.loadUserByUsername(username)

        // Validate the token
        if (!jwtService.validToken(refreshToken, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        // Load user data
        val user =
            userRepository.findByUsernameIgnoreCase(username)
                ?: throw NotFoundException("User not found: $username")

        val newAccessTokenCookie = generateAccessCookie(user)
        val newRefreshTokenCookie = generateRefreshCookie(user)

        response.addHeader("Set-Cookie", newAccessTokenCookie.toString())
        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString())

        return ResponseEntity.ok().build()
    }

    fun logoutUser(response: HttpServletResponse): ResponseEntity<Void> {
        val deleteAccess = buildClearedAccessCookie()
        val deleteRefresh = buildClearedRefreshToken()

        response.addHeader("Set-Cookie", deleteAccess.toString())
        response.addHeader("Set-Cookie", deleteRefresh.toString())

        return ResponseEntity.ok().build()
    }

    private fun generateAccessCookie(user: User): ResponseCookie {
        val accessToken =
            jwtService.generateToken(
                username = user.username,
                id = user.id!!,
                role = user.role,
                expirationMillis = accessTokenAge.inWholeMilliseconds,
            )

        return buildAccessCookie(accessToken)
    }

    private fun generateRefreshCookie(user: User): ResponseCookie {
        val refreshToken =
            jwtService.generateToken(
                username = user.username,
                id = user.id!!,
                role = user.role,
                expirationMillis = refreshTokenAge.inWholeMilliseconds,
            )

        return buildRefreshCookie(refreshToken)
    }

    companion object {
        private val accessTokenAge = 15.minutes
        private val refreshTokenAge = 30.days

        const val ACCESS_TOKEN_NAME = "access_token"
        const val REFRESH_TOKEN_NAME = "refresh_token"

        /**
         * Generates a new access cookie for a response header.
         *
         * @param token Java Web Token
         * @return Response cookie with access token.
         */
        private fun buildAccessCookie(token: String) =
            ResponseCookie
                .from(ACCESS_TOKEN_NAME, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(accessTokenAge.inWholeSeconds)
                .build()

        /**
         * Generates a new refresh cookie for a response header.
         *
         * @param token Java Web Token
         * @return Response cookie with access token
         */
        private fun buildRefreshCookie(token: String) =
            ResponseCookie
                .from(REFRESH_TOKEN_NAME, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(refreshTokenAge.inWholeSeconds)
                .build()

        /** Builds a cookie which essentially "unauthorizes" current access token. */
        private fun buildClearedAccessCookie() =
            ResponseCookie
                .from(ACCESS_TOKEN_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build()

        /** Builds a cookie which essentially "unauthorizes" the current refresh token. */
        private fun buildClearedRefreshToken() =
            ResponseCookie
                .from(REFRESH_TOKEN_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(0)
                .build()
    }
}
