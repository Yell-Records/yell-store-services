package com.yellrecords.services.artistpage.mapper

import com.yellrecords.services.artistpage.ArtistPage
import com.yellrecords.services.artistpage.dto.ArtistPageDto

object ArtistPageMapper {
    fun toDto(artistPage: ArtistPage) =
        ArtistPageDto(
            id = artistPage.id!!,
            slug = artistPage.slug,
            name = artistPage.name,
            bodyHtml = artistPage.bodyHtml,
            youtubeUrls = artistPage.youtubeUrls,
            categoryId = artistPage.categoryId,
            createdAt = artistPage.createdAt,
            updatedAt = artistPage.updatedAt,
        )
}
