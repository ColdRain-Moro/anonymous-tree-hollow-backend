package anonymous.tree.hollow.database.entity

import anonymous.tree.hollow.database.dto.UserDto
import anonymous.tree.hollow.user.UserType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * anonymous.tree.hollow.database.entity.UserEntity.kt
 * AnonymousTreeHollow
 *
 * 用户表中只存储本站用户
 *
 * @author 寒雨
 * @since 2022/10/28 下午6:01
 */
object TableUser : LongIdTable("user") {
    // 邮箱: 可能是校园邮箱，如果是校园邮箱则无需上传校园卡照片
    val email = varchar("email", 64).uniqueIndex()
    // md5散列后的密码
    val md5Password = varchar("md5_password", 32)
    // 盐值
    val salt = varchar("salt", 32)
    // 校园卡照片，如果不是校园邮箱就需要审核
    val idCardImgUrl = varchar("id_card_img_url", 256).nullable()
    // 是否可用
    val type = integer("type")
    // 加入时间
    val join = long("join")
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(TableUser)

    var email by TableUser.email
    var md5Password by TableUser.md5Password
    var salt by TableUser.salt
    var idCardImgUrl by TableUser.idCardImgUrl
    var join by TableUser.join
    private var type by TableUser.type

    var userType: UserType
        set(value) {
            type = value.code
        }
        get() = UserType.fromCode(type)

    fun dto(): UserDto {
        return transaction {
            UserDto(this@UserEntity.id.value, email, idCardImgUrl, userType.token, join)
        }
    }
}