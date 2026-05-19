package com.yellrecords.services.mail

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/email")
class EmailController(
    private val emailService: EmailService,
) {
    @PostMapping("/sendEmail")
    fun sendEmail(
        @RequestBody request: EmailDTO,
    ) {
        emailService.sendEmail(request)
    }
}
