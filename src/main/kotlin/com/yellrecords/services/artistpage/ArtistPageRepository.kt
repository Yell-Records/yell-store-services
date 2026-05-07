package com.yellrecords.services.artistpage

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistPageRepository : JpaRepository<ArtistPage, UUID> {
    fun findArtistPageBySlug(slug: String): ArtistPage?

    fun existsBySlug(slug: String): Boolean
}
