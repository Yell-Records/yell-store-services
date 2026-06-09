package com.yellrecords.services.itemlisting

import com.yellrecords.services.cart.CartItemRepository
import com.yellrecords.services.category.CategoryRepository
import com.yellrecords.services.exception.domain.BadRequestException
import com.yellrecords.services.exception.domain.NotFoundException
import com.yellrecords.services.itemlisting.dto.CreateListingRequest
import com.yellrecords.services.itemlisting.dto.ItemListingDto
import com.yellrecords.services.itemlisting.dto.ItemListingMapper
import com.yellrecords.services.itemlisting.dto.UpdateListingRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class ItemListingService(
    private val itemListingRepo: ItemListingRepository,
    private val categoryRepository: CategoryRepository,
    private val cartItemRepository: CartItemRepository,
) {
    /** Gets ALL listings. */
    fun getAllListings(): List<ItemListingDto> =
        itemListingRepo.findAll().map { listing ->
            ItemListingMapper.toDto(listing, listing.category())
        }

    /** Retrieves every active item listing. */
    fun getActiveListings(): List<ItemListingDto> =
        itemListingRepo.findAllActive().map { listing ->
            ItemListingMapper.toDto(listing, listing.category())
        }

    /**
     * Retrieves an item listing by its [ID][ItemListing.id].
     *
     * @throws NotFoundException If no item listing has the provided ID.
     */
    fun getListingById(id: UUID): ItemListingDto {
        val listing =
            itemListingRepo.findById(id).getOrElse {
                throw NotFoundException("Listing with ID not found: $id")
            }

        return ItemListingMapper.toDto(listing, listing.category())
    }

    /** Gets every listing associated with a category slug. The category must be active. */
    fun getListingsByCategorySlug(slug: String): List<ItemListingDto> {
        val lowerSlug = slug.lowercase()
        val category = categoryRepository.findCategoryBySlug(lowerSlug)

        if (category == null || !category.isActive) {
            throw NotFoundException("Category with Slug $lowerSlug not found.")
        }

        val listings =
            itemListingRepo.findByCategoryId(category.id!!).ifEmpty {
                return emptyList()
            }

        return listings.map { listing -> ItemListingMapper.toDto(listing, listing.category()) }
    }

    @Transactional
    fun updateListing(
        id: UUID,
        req: UpdateListingRequest,
    ): ResponseEntity<Void> {
        val listing =
            itemListingRepo.findById(id).getOrElse {
                throw NotFoundException("Item listing with ID $id not found")
            }

        var updated = false

        req.title?.let {
            listing.title = it
            updated = true
        }
        req.description?.let {
            listing.description = it
            updated = true
        }
        req.price?.let { price ->
            if (price <= BigDecimal.ZERO) {
                throw BadRequestException("Price must be greater than zero.")
            }

            listing.price = price
            updated = true
        }
        req.imageUrl?.let {
            listing.imageUrl = it
            updated = true
        }
        req.isActive?.let {
            listing.isActive = it

            if (!listing.isActive) {
                // Clear any cart items that currently hold this listing
                cartItemRepository.deleteCartItemsByListingId(listing.id!!)
            }

            updated = true
        }
        req.categorySlug?.let { slug ->
            val category = categoryRepository.findCategoryBySlug(slug)
            if (category == null || !category.isActive) {
                throw NotFoundException("Category with Slug $slug not found.")
            }

            listing.categoryId = category.id!!
            updated = true
        }

        return if (updated) {
            listing.updatedAt = OffsetDateTime.now()

            ResponseEntity.ok().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }

    /** Saves a new item listing entity to the database and returns the provided information. */
    fun createListing(request: CreateListingRequest): ItemListingDto {
        val categoryId =
            categoryRepository.findCategoryBySlug(request.categorySlug)?.id
                ?: throw NotFoundException("Category with slug ${request.categorySlug} not found.")

        val listingEntry =
            ItemListing(
                title = request.title,
                description = request.description,
                imageUrl = request.imageUrl,
                price = request.price,
                categoryId = categoryId,
            )

        val saved = itemListingRepo.save(listingEntry)

        return ItemListingMapper.toDto(saved, saved.category())
    }

    private fun ItemListing.category() =
        categoryRepository.findById(this.categoryId).getOrElse {
            throw NotFoundException(
                "Category with ID (${this.categoryId}) on listing (${this.id}) not found",
            )
        }
}
