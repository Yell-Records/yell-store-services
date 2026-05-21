package com.yellrecords.services.policies

import com.yellrecords.services.config.PolicyDocsProperties
import com.yellrecords.services.util.HtmlUtil
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

        val cleanContent = HtmlUtil.cleanHtml(content)

        Files.writeString(path, cleanContent)
    }
}
