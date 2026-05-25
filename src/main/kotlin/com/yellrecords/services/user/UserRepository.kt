package com.yellrecords.services.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsernameIgnoreCase(username: String): User?

    @Query(
        """
            select u from User u order by createdAt desc limit 1
        """,
    )
    fun findAdmin(): User?

    fun existsByUsernameIgnoreCase(username: String): Boolean
}
