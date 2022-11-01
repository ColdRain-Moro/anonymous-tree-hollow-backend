package anonymous.tree.hollow.database.service

import anonymous.tree.hollow.database.dto.VerifyRequestDto
import anonymous.tree.hollow.database.entity.IDCardVerifyQueueEntity
import anonymous.tree.hollow.database.entity.TableIDCardVerifyQueue
import anonymous.tree.hollow.database.entity.TableUser
import anonymous.tree.hollow.database.entity.UserEntity
import anonymous.tree.hollow.service.CDNService
import anonymous.tree.hollow.service.EmailService
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * anonymous.tree.hollow.database.service.VerifyService.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/31 下午11:42
 */
@Service
class VerifyService {

    @Autowired
    private lateinit var cdnService: CDNService
    @Autowired
    private lateinit var emailService: EmailService
    @Autowired
    private lateinit var taskExecutor: ScheduledExecutorService

    @Value("\${anonymous-tree-hollow.cdn.id-card-scope}")
    private lateinit var idCardScope: String

    private val verifyCodeCache = ConcurrentHashMap<String, String>()
    private val verifyCodeValidityCache = ConcurrentHashMap<String, ScheduledFuture<*>>()

    fun verify(email: String, code: String): Boolean {
        val success = verifyCodeCache[email] == code
        if (success) {
            verifyCodeValidityCache[email]?.cancel(true)
            verifyCodeValidityCache.remove(email)
            verifyCodeCache.remove(email)
        }
        return success
    }

    fun sendVerifyCode(email: String, code: String) {
        emailService.sendEmail(
            email,
            "[AnonymousTreeHollow] Mailbox verification code",
            "你的邮箱验证码是 $code ，若非本人操作请忽略。"
        )
        verifyCodeCache[email] = code
        verifyCodeValidityCache[email] = taskExecutor.schedule({
            verifyCodeCache.remove(email)
        }, 10, TimeUnit.MINUTES)
    }

    fun sendRequest(email: String, image: MultipartFile): Boolean {
        // 图片拓展名
        val suffix = image.originalFilename?.split(".")?.last() ?: "jpg"
        val uuid = UUID.randomUUID().toString()
        val url = cdnService.putObject(idCardScope, "$uuid.$suffix", image.inputStream, image.size)
        return transaction {
            val user = UserEntity.find { TableUser.email eq email }.firstOrNull() ?: return@transaction false
            IDCardVerifyQueueEntity.new {
                this.user = user
                imageUrl = url
                time = System.currentTimeMillis()
            }
            true
        }
    }

    fun getRequests(offset: Long, limit: Int): List<VerifyRequestDto> {
        return transaction {
            IDCardVerifyQueueEntity.all()
                .limit(limit, offset)
                .orderBy(TableIDCardVerifyQueue.time to SortOrder.DESC)
                .map { it.dto() }
        }
    }

    fun rejectRequest(id: Long) {
        transaction {
            val entity = IDCardVerifyQueueEntity.findById(id) ?: return@transaction
            emailService.sendEmail(
                entity.user.email,
                "[AnonymousTreeHollow] Your IDCard verification has been reject!",
                "抱歉，您的校园卡认证未审核通过。请您确保照片清晰并且是一张有效的本校校园卡。"
            )
            entity.delete()
        }
    }

    fun acceptRequest(id: Long) {
        transaction {
            val entity = IDCardVerifyQueueEntity.findById(id) ?: return@transaction
            emailService.sendEmail(
                entity.user.email,
                "[AnonymousTreeHollow] Your IDCard verification has been approved!",
                "恭喜！您的校园卡认证已经通过！现在您的帐号有访问树洞的权限了！"
            )
            entity.delete()
        }
    }

    fun hasRequested(email: String): Boolean {
        return transaction {
            val user = UserEntity.find { TableUser.email eq email }.firstOrNull() ?: return@transaction false
            !IDCardVerifyQueueEntity.find { TableIDCardVerifyQueue.user eq user.id }.empty()
        }
    }
}