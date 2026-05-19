package com.yellrecords.services.mail

data class EmailDTO(
    val to: String,
    val subject: String,
    val body: String,
)
