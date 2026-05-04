package com.yellrecords.services.auth

import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordHashPrinter(
    private val encoder: PasswordEncoder,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val raw = System.getenv("GENERATE_PASSWORD")

        if (!raw.isNullOrBlank()) {
            val hash = encoder.encode(raw)
            println("Generated hash for provided password")
            println(hash)
        }
    }
}
