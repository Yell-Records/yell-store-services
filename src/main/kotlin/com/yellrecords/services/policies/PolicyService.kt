package com.yellrecords.services.policies

import com.yellrecords.services.config.PolicyDocsProperties
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths

@Service
class PolicyService(
    private val policyDocsProperties: PolicyDocsProperties,
) {
    fun read(filename: String): String {
        val path = Paths.get(policyDocsProperties.path, filename)

        return Files.readString(path)
    }

    fun write(
        filename: String,
        content: String,
    ) {
        val path = Paths.get(policyDocsProperties.path, filename)

        Files.writeString(path, content)
    }
}
