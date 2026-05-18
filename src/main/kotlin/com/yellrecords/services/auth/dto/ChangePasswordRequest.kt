package com.yellrecords.services.auth.dto

data class ChangePasswordRequest(
    val rawCurrent: String,
    val rawNew: String,
    val rawNew2: String,
)
