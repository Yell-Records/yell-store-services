package com.yellrecords.services.exception.domain

/**
 * Exception for attempts to save an entity which already exists.
 *
 * Status code: 409
 */
class ConflictException(
    override val message: String,
) : RuntimeException(message)
