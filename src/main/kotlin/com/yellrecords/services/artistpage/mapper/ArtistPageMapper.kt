package com.yellrecords.services.artistpage.mapper

import com.yellrecords.services.artistpage.ArtistPage
import com.yellrecords.services.artistpage.dto.ArtistPageDto

object ArtistPageMapper {
    fun toDto(
        artistPage: ArtistPage,
        categorySlug: String,
    ) = ArtistPageDto(
        id = artistPage.id!!,
        slug = artistPage.slug,
        name = artistPage.name,
        bodyHtml = artistPage.bodyHtml,
        categorySlug = categorySlug,
        createdAt = artistPage.createdAt,
        updatedAt = artistPage.updatedAt,
    )
}
