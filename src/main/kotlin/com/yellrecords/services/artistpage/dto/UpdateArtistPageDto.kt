package com.yellrecords.services.artistpage.dto

data class UpdateArtistPageDto(
    val name: String? = null,
    val slug: String? = null,
    val bodyHtml: String? = null,
    val youtubeUrls: List<String>? = null,
    val categorySlug: String? = null,
)
