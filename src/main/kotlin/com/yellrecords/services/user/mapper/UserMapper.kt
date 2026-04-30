package com.yellrecords.services.user.mapper

import com.yellrecords.services.user.User
import com.yellrecords.services.user.dto.UserDto

object UserMapper {
    fun toDto(entity: User): UserDto =
        UserDto(
            id = entity.id!!,
            username = entity.username,
            email = entity.email,
            createdAt = entity.createdAt,
            role = entity.role,
        )
}
