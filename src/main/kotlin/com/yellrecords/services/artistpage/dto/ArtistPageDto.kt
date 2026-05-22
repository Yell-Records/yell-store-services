package com.yellrecords.services.artistpage.dto

import java.time.OffsetDateTime
import java.util.UUID

data class ArtistPageDto(
    val id: UUID,
    val slug: String,
    val name: String,
    val bodyHtml: String,
    val categorySlug: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)
