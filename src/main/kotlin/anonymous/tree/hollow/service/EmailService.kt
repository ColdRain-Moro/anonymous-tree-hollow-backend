package anonymous.tree.hollow.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledExecutorService

/**
 * anonymous.tree.hollow.service.EmailService.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/1 上午12:13
 */
@Service
class EmailService {
    @Autowired
    private lateinit var mailSender: JavaMailSender
    @Value("\${spring.mail.username}")
    private lateinit var sender: String

    fun sendEmail(to: String, subject: String, content: String) {
        val message = SimpleMailMessage().apply {
            setFrom(sender)
            setTo(to)
            setSubject(subject)
            setText(content)
        }
        mailSender.send(message)
    }
}