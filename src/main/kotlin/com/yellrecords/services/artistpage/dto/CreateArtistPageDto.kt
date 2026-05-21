package com.yellrecords.services.artistpage.dto

data class CreateArtistPageDto(
    val slug: String,
    val name: String,
    val bodyHtml: String,
    val categorySlug: String,
)
