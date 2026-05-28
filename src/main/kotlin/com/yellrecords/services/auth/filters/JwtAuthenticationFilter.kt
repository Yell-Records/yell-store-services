package com.yellrecords.services.auth.filters

import com.yellrecords.services.auth.AuthService
import com.yellrecords.services.auth.CustomUserDetailsService
import com.yellrecords.services.auth.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessToken =
            request.cookies?.firstOrNull { it.name == AuthService.ACCESS_TOKEN_NAME }?.value

        accessToken?.let { token ->
            val tokenUsername = jwtService.extractUsername(token)

            tokenUsername?.let { username ->
                val userDetails = userDetailsService.loadUserByUsername(username)

                val auth =
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}
