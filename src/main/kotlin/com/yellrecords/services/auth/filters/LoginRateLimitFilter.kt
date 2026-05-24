package com.yellrecords.services.auth.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap

/** Prevents bulk requests from accessing the login endpoint. */
@Component
class LoginRateLimitFilter : OncePerRequestFilter() {
    private val attempts = ConcurrentHashMap<String, LoginAttempt>()
    private val maxAttempts = 5
    private val windowMs = 60_000L // 1 minute

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (request.requestURI.contains("/login") && request.method == HttpMethod.POST.name()) {
            val ip = request.remoteAddr
            val now = System.currentTimeMillis()

            val attempt =
                attempts.compute(ip) { _, existing ->
                    val current = existing ?: LoginAttempt(now, 0)

                    if (now - current.startTime > windowMs) {
                        LoginAttempt(now, 1)
                    } else {
                        current.copy(count = current.count + 1)
                    }
                }!!

            if (attempt.count > maxAttempts) {
                response.status = HttpStatus.TOO_MANY_REQUESTS.value()
                response.writer.write("Too many login attempts. Try again later.")
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    data class LoginAttempt(
        val startTime: Long,
        val count: Int,
    )
}
