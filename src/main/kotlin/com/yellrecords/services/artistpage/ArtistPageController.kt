package com.yellrecords.services.artistpage

import com.yellrecords.services.artistpage.dto.ArtistPageDto
import com.yellrecords.services.artistpage.dto.CreateArtistPageDto
import com.yellrecords.services.artistpage.dto.UpdateArtistPageDto
import com.yellrecords.services.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/artist-pages")
class ArtistPageController(
    private val artistPageService: ArtistPageService,
) {
    @GetMapping fun getArtistPages(): List<ArtistPageDto> = artistPageService.getArtistPages()

    @GetMapping("/slug/{slug}")
    fun getArtistPageBySlug(
        @PathVariable slug: String,
    ): ArtistPageDto = artistPageService.getArtistPageBySlug(slug)

    @PostMapping
    @RolesAllowed(UserRole.ADMIN)
    fun createArtistPage(
        @RequestBody req: CreateArtistPageDto,
    ): ResponseEntity<ArtistPageDto> {
        val saved = artistPageService.createArtistPage(req)

        return ResponseEntity(saved, HttpStatus.CREATED)
    }

    @PatchMapping("/{id}")
    @RolesAllowed(UserRole.ADMIN)
    fun updateArtistPage(
        @PathVariable id: UUID,
        @RequestBody req: UpdateArtistPageDto,
    ): ResponseEntity<Void> = artistPageService.updateArtistPage(id, req)

    @DeleteMapping("/{id}")
    @RolesAllowed(UserRole.ADMIN)
    fun deleteArtistPage(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> = artistPageService.deleteArtistPage(id)
}
