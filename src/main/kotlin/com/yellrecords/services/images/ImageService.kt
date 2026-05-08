package com.yellrecords.services.images

import com.yellrecords.services.config.ImageUploadProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class ImageService(
    private val imageConfig: ImageUploadProperties,
) {
    private val uploadDir: Path by lazy {
        val dir = Paths.get(imageConfig.uploadDir!!)

        if (!Files.exists(dir)) {
            Files.createDirectories(dir)
        }

        dir
    }

    fun saveImage(file: MultipartFile): String =
        when (imageConfig.provider) {
            ImageProvider.LOCAL -> saveToLocal(file)
            ImageProvider.S3 -> saveToS3(file)
        }

    private fun saveToLocal(file: MultipartFile): String {
        val filename = "${UUID.randomUUID()}-${file.originalFilename}"
        val target = uploadDir.resolve(filename)

        Files.copy(file.inputStream, target)

        return filename
    }

    private fun saveToS3(file: MultipartFile): String {
        // TODO Save to S3
        val filename = "${UUID.randomUUID()}-${file.originalFilename}"

        return "${imageConfig.baseUrl}/$filename"
    }
}
