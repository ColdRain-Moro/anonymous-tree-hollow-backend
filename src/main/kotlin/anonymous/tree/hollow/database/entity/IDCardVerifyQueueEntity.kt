package anonymous.tree.hollow.database.entity

import anonymous.tree.hollow.database.dto.VerifyRequestDto
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * anonymous.tree.hollow.database.entity.IDCardVerifyQueueEntity.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/31 下午11:58
 */
object TableIDCardVerifyQueue : LongIdTable("id_card_verify_queue") {
    val user = reference("user", TableUser).uniqueIndex()
    val imageUrl = text("image_url")
    // 请求时间
    val time = long("time")
}

class IDCardVerifyQueueEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<IDCardVerifyQueueEntity>(TableIDCardVerifyQueue)

    var user by UserEntity referencedOn TableIDCardVerifyQueue.user
    var imageUrl by TableIDCardVerifyQueue.imageUrl
    var time by TableIDCardVerifyQueue.time

    fun dto(): VerifyRequestDto {
        return VerifyRequestDto(id.value, imageUrl, user.dto(), time)
    }
}