package com.yellrecords.services.user.dto

import java.time.OffsetDateTime
import java.util.UUID

data class UserDto(
    val id: UUID,
    val username: String,
    val email: String?,
    val createdAt: OffsetDateTime,
    val role: String,
)
