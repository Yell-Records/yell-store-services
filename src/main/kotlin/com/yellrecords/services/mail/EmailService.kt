package com.yellrecords.services.mail

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
) {
    fun sendEmail(request: EmailDTO) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(request.to)
        helper.setSubject(request.subject)
        helper.setText(request.body, false)

        mailSender.send(message)
    }
}
