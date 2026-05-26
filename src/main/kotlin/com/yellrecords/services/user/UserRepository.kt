package com.yellrecords.services.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsernameIgnoreCase(username: String): User?

    @Query(
        """
            SELECT u FROM User u
            WHERE u.role = "ADMIN"
            ORDER BY u.createdAt DESC LIMIT 1
        """,
    )
    fun findFirstAdmin(): User?

    fun existsByUsernameIgnoreCase(username: String): Boolean
}
