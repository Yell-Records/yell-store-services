package com.yellrecords.services.policies

import com.yellrecords.services.config.PolicyDocsProperties
import com.yellrecords.services.util.HtmlUtil
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.file.Files
import java.nio.file.Paths

@Service
class PolicyService(
    private val policyDocsProperties: PolicyDocsProperties,
    private val s3Client: S3Client,
) {
    fun read(filename: String) =
        when (policyDocsProperties.provider) {
            PolicyProvider.LOCAL -> readLocal(filename)
            PolicyProvider.S3 -> readS3(filename)
        }

    private fun readLocal(filename: String): String {
        val path = Paths.get(policyDocsProperties.path, filename)

        return Files.readString(path)
    }

    private fun readS3(filename: String): String {
        val obj =
            s3Client.getObject(
                GetObjectRequest
                    .builder()
                    .bucket(policyDocsProperties.bucket)
                    .key(filename)
                    .build(),
            )

        return obj.readAllBytes().toString(Charsets.UTF_8)
    }

    fun write(
        filename: String,
        content: String,
    ) {
        val cleanedContent = HtmlUtil.cleanHtml(content)

        when (policyDocsProperties.provider) {
            PolicyProvider.LOCAL -> writeLocal(filename, cleanedContent)
            PolicyProvider.S3 -> writeS3(filename, cleanedContent)
        }
    }

    private fun writeLocal(
        filename: String,
        content: String,
    ) {
        val path = Paths.get(policyDocsProperties.path, filename)

        Files.writeString(path, content)
    }

    private fun writeS3(
        filename: String,
        content: String,
    ) = s3Client.putObject(
        PutObjectRequest
            .builder()
            .bucket(policyDocsProperties.bucket)
            .key(filename)
            .contentType("text/html")
            .build(),
        RequestBody.fromString(content),
    )
}
