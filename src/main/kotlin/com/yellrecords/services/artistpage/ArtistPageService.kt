package com.yellrecords.services.artistpage

import com.yellrecords.services.artistpage.dto.ArtistPageDto
import com.yellrecords.services.artistpage.dto.CreateArtistPageDto
import com.yellrecords.services.artistpage.dto.UpdateArtistPageDto
import com.yellrecords.services.artistpage.mapper.ArtistPageMapper
import com.yellrecords.services.category.CategoryRepository
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.exception.ConflictException
import com.yellrecords.services.exception.NotFoundException
import com.yellrecords.services.util.SLUG_REGEX
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class ArtistPageService(
    private val artistPageRepository: ArtistPageRepository,
    private val categoryRepository: CategoryRepository,
) {
    fun getArtistPageBySlug(slug: String): ArtistPageDto {
        val page =
            artistPageRepository.findArtistPageBySlug(slug)
                ?: throw NotFoundException("Could not find artist page with slug $slug")

        return ArtistPageMapper.toDto(page)
    }

    fun getArtistPages() = artistPageRepository.findAll().map { ArtistPageMapper.toDto(it) }

    fun createArtistPage(request: CreateArtistPageDto): ArtistPageDto {
        val slug = request.slug.lowercase()

        enforceValidSlug(slug)

        val category =
            categoryRepository.findCategoryBySlug(request.categorySlug)
                ?: throw NotFoundException(
                    "Could not find category with slug ${request.categorySlug}.",
                )

        val entity =
            ArtistPage(
                slug = slug,
                name = request.name,
                bodyHtml = request.bodyHtml,
                categoryId = category.id!!,
                youtubeUrls = request.youtubeUrls,
            )

        val saved = artistPageRepository.save(entity)

        return ArtistPageMapper.toDto(saved)
    }

    @Transactional
    fun updateArtistPage(
        pageId: UUID,
        updateReq: UpdateArtistPageDto,
    ): ResponseEntity<Void> {
        val artistPage =
            artistPageRepository.findById(pageId).getOrElse {
                throw NotFoundException("Could not find artist page with id $pageId.")
            }

        var updated = false

        updateReq.name?.let {
            artistPage.name = it
            updated = true
        }

        updateReq.slug?.let {
            val slug = it.lowercase()

            enforceValidSlug(slug)

            artistPage.slug = slug
            updated = true
        }

        updateReq.bodyHtml?.let {
            artistPage.bodyHtml = it
            updated = true
        }

        updateReq.youtubeUrls?.let {
            artistPage.youtubeUrls = it
            updated = true
        }

        updateReq.categorySlug?.let {
            val category =
                categoryRepository.findCategoryBySlug(it)
                    ?: throw NotFoundException("Could not find category with slug $it")

            artistPage.categoryId = category.id!!
            updated = true
        }

        return if (updated) {
            artistPage.updatedAt = OffsetDateTime.now()

            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @Transactional
    fun deleteArtistPage(pageId: UUID): ResponseEntity<Void> {
        artistPageRepository.deleteById(pageId)

        return ResponseEntity.noContent().build()
    }

    private fun enforceValidSlug(slug: String) {
        if (!SLUG_REGEX.matches(slug)) {
            throw BadRequestException("Slug '$slug' must match regex: ${SLUG_REGEX.pattern}")
        }

        if (artistPageRepository.existsBySlug(slug)) {
            throw ConflictException("Artist page with slug $slug already exists.")
        }
    }
}
