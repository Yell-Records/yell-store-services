package com.yellrecords.services.artistpage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ArtistPageRepository : JpaRepository<ArtistPage, UUID> {
    fun findArtistPageBySlug(slug: String): ArtistPage?

    fun existsBySlug(slug: String): Boolean

    @Query(
        """
            SELECT a FROM ArtistPage a
            ORDER BY a.name
        """,
    )
    fun findAllSortedByName(): List<ArtistPage>
}
