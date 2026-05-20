package com.yellrecords.services.policies

import com.yellrecords.services.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/policies")
class PolicyController(
    private val policyService: PolicyService,
) {
    @GetMapping("/{name}")
    fun getPolicy(
        @PathVariable name: String,
    ): ResponseEntity<String> {
        val content = policyService.read("$name.html")

        return ResponseEntity.ok(content)
    }

    @PutMapping("/{name}")
    @RolesAllowed(UserRole.ADMIN)
    fun updatePolicy(
        @PathVariable name: String,
        @RequestBody content: String,
    ): ResponseEntity<Void> {
        policyService.write("$name.html", content)

        return ResponseEntity.ok().build()
    }
}
