package com.yellrecords.services.user

import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.exception.ConflictException
import com.yellrecords.services.exception.NotFoundException
import com.yellrecords.services.user.dto.RegistrationInfo
import com.yellrecords.services.user.dto.UpdateEmailRequest
import com.yellrecords.services.user.dto.UserDto
import com.yellrecords.services.user.mapper.UserMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val repo: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    companion object {
        private val EMAIL_REGEX = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    }

    /**
     * Saves a new user object to the database using the provided information.
     *
     * @throws ConflictException If a user exists using the requested username.
     */
    fun createUser(registerInfo: RegistrationInfo): UserDto {
        if (repo.existsByUsernameIgnoreCase(registerInfo.username)) {
            throw ConflictException("User already exists.")
        }

        val userEntity =
            repo.save(
                User(
                    username = registerInfo.username,
                    passwordHash = passwordEncoder.encode(registerInfo.rawPassword)!!,
                    email = registerInfo.email,
                ),
            )

        return UserMapper.toDto(userEntity)
    }

    @Transactional
    fun updateEmail(
        userId: UUID,
        emailRequest: UpdateEmailRequest,
    ): ResponseEntity<Void> {
        val user =
            repo.findById(userId).getOrElse {
                throw NotFoundException("User with id $userId does not exist.")
            }

        if (!EMAIL_REGEX.matches(emailRequest.newEmail)) {
            throw BadRequestException("Email must abide by regex.")
        }

        user.email?.let {
            if (emailRequest.newEmail.equals(it, ignoreCase = true)) {
                throw BadRequestException("Email is the same.")
            }
        }

        user.email = emailRequest.newEmail.lowercase()

        return ResponseEntity.ok().build()
    }

    /**
     * Retrieves a user by their [ID][User.id].
     *
     * @throws NotFoundException If there is no user with the provided ID.
     */
    fun getUserById(id: UUID): UserDto {
        val user =
            repo.findById(id).orElseThrow { NotFoundException("User with id $id not found.") }

        return UserMapper.toDto(user)
    }
}
