package anonymous.tree.hollow.database.entity

import anonymous.tree.hollow.database.dto.CommentDto
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * anonymous.tree.hollow.database.entity.CommentEntity.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/30 下午1:28
 */
object TableComment : LongIdTable("comment") {
    val senderMd5 = varchar("sender_md5", 32)
    val uuid = uuid("uuid").uniqueIndex()
    // 哪个帖子下的评论
    val post = reference("post", TablePost)
    val postTime = long("post_time")
    // 来源 例: anonymous.tree.hollow
    val originSite = varchar("origin_site", 128)
    // 来源名称 例: 重庆邮电大学
    val originName = varchar("origin_name", 128)
    val content = text("content")
    val image = varchar("image", 256).nullable()
    // 回复某评论
    val reply = reference("reply", TableComment).nullable()
}

class CommentEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : LongEntityClass<CommentEntity>(TableComment)

    var senderMd5 by TableComment.senderMd5
    var post by PostEntity referencedOn TableComment.post
    var postTime by TableComment.postTime
    var originSite by TableComment.originSite
    var originName by TableComment.originName
    var content by TableComment.content
    var image by TableComment.image
    var reply by CommentEntity optionalReferencedOn TableComment.reply

    fun dto(): CommentDto {
        return transaction {
            CommentDto(
                id = this@CommentEntity.id.value,
                senderMd5 = senderMd5,
                post = post.dto(),
                postTime = System.currentTimeMillis(),
                originSite = originSite,
                originName = originName,
                content = content,
                image = image,
                reply = reply?.dto()
            )
        }
    }
}