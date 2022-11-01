package anonymous.tree.hollow.database.service

import anonymous.tree.hollow.database.entity.TableUser
import anonymous.tree.hollow.database.entity.UserEntity
import anonymous.tree.hollow.user.UserType
import anonymous.tree.hollow.utils.isStuEmail
import anonymous.tree.hollow.utils.md5
import anonymous.tree.hollow.utils.randomSalt
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * anonymous.tree.hollow.database.service.UserService.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/28 下午10:23
 */
@Service
class UserService {

    private val userTable = TableUser

    fun login(email: String, password: String): Long {
        return transaction {
            val result = UserEntity.find { userTable.email eq email }.firstOrNull() ?: return@transaction -1
            if ((password +  result.salt).md5() == result.md5Password) result.id.value else -1
        }
    }

    fun register(email: String, password: String): Long {
        return transaction {
            if (!UserEntity.find { userTable.email eq email }.empty()) return@transaction -1
            UserEntity.new {
                this.email = email
                this.salt = randomSalt()
                this.md5Password = (password + salt).md5()
                userType = if (email.isStuEmail()) UserType.USER else UserType.UNAUTHORIZED
            }.id.value
        }
    }

    fun changePassword(email: String, newPassword: String): Boolean {
        return transaction {
            val result = UserEntity.find { userTable.email eq email }
            if (result.empty()) return@transaction false
            val user = result.first()
            user.md5Password == (newPassword + user.salt).md5()
        }
    }

    fun changePassword(userId: Long, oldPassword: String, newPassword: String): Boolean {
        return transaction {
            val user = UserEntity.findById(userId) ?: return@transaction false
            if (user.md5Password != (oldPassword + user.salt).md5()) return@transaction false
            user.md5Password == (newPassword + user.salt).md5()
        }
    }

    fun getUserType(email: String): UserType? {
        return transaction {
            UserEntity.find { userTable.email eq email }.firstOrNull()?.userType
        }
    }
}