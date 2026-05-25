package com.yellrecords.services.artistpage

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "artist_pages")
class ArtistPage(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: UUID? = null,
    @Column(nullable = false) var slug: String,
    @Column(nullable = false) var name: String,
    @Column(name = "body_html", nullable = false) var bodyHtml: String,
    @Column(name = "category_id", nullable = false) var categoryId: UUID,
    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
