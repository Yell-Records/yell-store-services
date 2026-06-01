package com.yellrecords.services.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class JwtService(
    @Value($$"${jwt.secret}") private val secretKey: String,
) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    /**
     * Extracts the subject from a Java Web Token.
     *
     * @param token Java Web Token to extract from.
     * @return Username in token, or null if the token is invalid or expired.
     */
    fun extractUsername(token: String): String? =
        try {
            extractAllClaims(token).subject
        } catch (_: Exception) {
            null // No username
        }

    /**
     * Generates a Java Web Token string which expires in 24 hours.
     *
     * @param username Subject of the token
     * @param id The user's ID
     * @param role The user's role
     * @param expirationMillis Expiration time in milliseconds
     */
    fun generateToken(
        username: String,
        id: UUID,
        role: String,
        expirationMillis: Long,
    ): String {
        val now = Date()
        val expiry = Date(now.time + expirationMillis)

        return Jwts
            .builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiry)
            .claim("uid", id)
            .claim("role", role)
            .signWith(key)
            .compact()
    }

    /** Checks if the token associated with user details is valid. */
    fun validToken(
        token: String,
        userDetails: UserDetails,
    ): Boolean {
        val username = extractUsername(token)

        return username == userDetails.username && !expiredToken(token)
    }

    private fun expiredToken(token: String): Boolean = extractExpiration(token).before(Date())

    /** Retrieves the expiration date on a token as a [Date]. */
    private fun extractExpiration(token: String): Date = extractClaim(token) { it.expiration }

    private fun <T> extractClaim(
        token: String,
        resolver: (Claims) -> T,
    ): T {
        val claims = extractAllClaims(token)
        return resolver(claims)
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}
