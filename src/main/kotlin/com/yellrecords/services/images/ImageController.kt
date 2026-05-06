package com.yellrecords.services.images

import com.yellrecords.services.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService,
) {
    @PostMapping("/upload")
    @RolesAllowed(UserRole.ADMIN)
    fun uploadImage(
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> {
        val url = imageService.saveImage(file)

        return ResponseEntity(url, HttpStatus.CREATED)
    }
}
