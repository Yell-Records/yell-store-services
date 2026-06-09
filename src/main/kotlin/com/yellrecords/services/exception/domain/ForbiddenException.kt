package com.yellrecords.services.exception.domain

/**
 * Exception which is thrown when logic fails to pass checks like business rules.
 *
 * Status code: 403
 */
class ForbiddenException(
    override val message: String,
) : RuntimeException(message)
