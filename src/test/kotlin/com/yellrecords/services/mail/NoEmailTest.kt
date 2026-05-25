package com.yellrecords.services.mail

import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import java.io.InputStream
import java.util.Properties

@TestConfiguration
class NoEmailTest {
    @Bean
    fun mailSender(): JavaMailSender =
        object : JavaMailSender {
            override fun createMimeMessage(): MimeMessage = MimeMessage(Session.getDefaultInstance(Properties()))

            override fun createMimeMessage(contentStream: InputStream): MimeMessage =
                MimeMessage(Session.getDefaultInstance(Properties()), contentStream)

            override fun send(mimeMessage: MimeMessage) {}

            override fun send(vararg mimeMessages: MimeMessage) {}

            override fun send(simpleMessage: SimpleMailMessage) {}

            override fun send(vararg simpleMessages: SimpleMailMessage) {}
        }
}
