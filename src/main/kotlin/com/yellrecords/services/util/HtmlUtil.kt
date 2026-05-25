package com.yellrecords.services.util

object HtmlUtil {
    fun cleanHtml(html: String): String =
        html
            .trim()
            // Normalize non-breaking spaces and special spacing entities
            .replace(Regex("&nbsp;|&ensp;|&emsp;|&#160;", RegexOption.IGNORE_CASE), " ")
            // Collapse multiple spaces into a single space
            .replace(Regex(" {2,}"), " ")
            // Remove inline white-space: nowrap
            .replace(Regex("white-space:\\s*nowrap;?", RegexOption.IGNORE_CASE), "")
            // Remove fixed widths that break wrapping
            .replace(Regex("width:\\s*\\d+px;?", RegexOption.IGNORE_CASE), "")
            .replace(Regex("min-width:\\s*\\d+px;?", RegexOption.IGNORE_CASE), "")
            // Remove empty spans left behind
            .replace(Regex("<span[^>]*></span>", RegexOption.IGNORE_CASE), "")
}
