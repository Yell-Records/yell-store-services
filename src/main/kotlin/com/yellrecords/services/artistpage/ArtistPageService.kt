package com.yellrecords.services.artistpage

import com.yellrecords.services.artistpage.dto.ArtistPageDto
import com.yellrecords.services.artistpage.dto.CreateArtistPageDto
import com.yellrecords.services.artistpage.dto.UpdateArtistPageDto
import com.yellrecords.services.artistpage.mapper.ArtistPageMapper
import com.yellrecords.services.category.CategoryRepository
import com.yellrecords.services.exception.BadRequestException
import com.yellrecords.services.exception.ConflictException
import com.yellrecords.services.exception.NotFoundException
import com.yellrecords.services.util.HtmlUtil
import com.yellrecords.services.util.SLUG_REGEX
import org.springframework.http.HttpStatus
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

        val category =
            categoryRepository.findById(page.categoryId).getOrElse {
                throw NotFoundException("Could not find category with id ${page.categoryId}")
            }

        return ArtistPageMapper.toDto(page, category.slug)
    }

    fun getArtistPages() =
        artistPageRepository.findAllSortedByName().map { page ->
            val category =
                categoryRepository.findById(page.categoryId).getOrElse {
                    throw NotFoundException("Could not find category with id ${page.categoryId}")
                }

            ArtistPageMapper.toDto(page, category.slug)
        }

    fun createArtistPage(request: CreateArtistPageDto): ArtistPageDto {
        val slug = request.slug.lowercase()

        enforceValidSlug(slug)

        val category =
            categoryRepository.findCategoryBySlug(request.categorySlug)
                ?: throw NotFoundException(
                    "Could not find category with slug ${request.categorySlug}.",
                )

        val cleanHtml = HtmlUtil.cleanHtml(request.bodyHtml)

        val entity =
            ArtistPage(
                slug = slug,
                name = request.name,
                bodyHtml = cleanHtml,
                categoryId = category.id!!,
            )

        val saved = artistPageRepository.save(entity)

        return ArtistPageMapper.toDto(saved, category.slug)
    }

    @Transactional
    fun updateArtistPage(
        pageId: UUID,
        updateReq: UpdateArtistPageDto,
    ): ResponseEntity<ArtistPageDto> {
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
            artistPage.bodyHtml = HtmlUtil.cleanHtml(it)

            updated = true
        }

        updateReq.categorySlug?.let {
            val category =
                categoryRepository.findCategoryBySlug(it)
                    ?: throw NotFoundException("Could not find category with slug $it")

            artistPage.categoryId = category.id!!
            updated = true
        }

        val category =
            categoryRepository.findById(artistPage.categoryId).getOrElse {
                throw NotFoundException("Could not find category with id ${artistPage.categoryId}")
            }

        val dto = ArtistPageMapper.toDto(artistPage, category.slug)

        return if (updated) {
            artistPage.updatedAt = OffsetDateTime.now()

            ResponseEntity(dto, HttpStatus.OK)
        } else {
            ResponseEntity(dto, HttpStatus.NO_CONTENT)
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
