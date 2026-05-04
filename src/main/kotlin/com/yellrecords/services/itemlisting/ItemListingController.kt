package com.yellrecords.services.itemlisting

import com.yellrecords.services.itemlisting.dto.CreateListingRequest
import com.yellrecords.services.itemlisting.dto.ItemListingDto
import com.yellrecords.services.itemlisting.dto.UpdateListingRequest
import com.yellrecords.services.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/item-listings")
class ItemListingController(
    private val service: ItemListingService,
) {
    @GetMapping fun getItemListings(): List<ItemListingDto> = service.getAllListings()

    @GetMapping("/category/{slug}")
    fun getItemsByCategory(
        @PathVariable slug: String,
    ): List<ItemListingDto> = service.getListingsByCategorySlug(slug)

    @GetMapping("/{listingId}")
    fun getItemListing(
        @PathVariable listingId: UUID,
    ): ItemListingDto = service.getListingById(listingId)

    @RolesAllowed(UserRole.ADMIN)
    @PostMapping
    fun createListing(
        @RequestBody request: CreateListingRequest,
    ): ResponseEntity<ItemListingDto> {
        val item = service.createListing(request)

        return ResponseEntity(item, HttpStatus.CREATED)
    }

    @RolesAllowed(UserRole.ADMIN)
    @PatchMapping("/{listingId}")
    fun editListing(
        @PathVariable listingId: UUID,
        @RequestBody updateListing: UpdateListingRequest,
    ) = service.updateListing(listingId, updateListing)
}
