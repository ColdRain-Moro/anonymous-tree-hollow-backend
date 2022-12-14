package anonymous.tree.hollow.database.entity

import anonymous.tree.hollow.database.dto.PostDto
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table.Dual.index
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * anonymous.tree.hollow.database.entity.PostEntity.kt
 * AnonymousTreeHollow
 *
 * 帖子是全部站共享，但是其中的评论是各存各的，使用评论相关的接口时会重定向到目标站的接口
 * 由于可能有多个后端，所以id的值并不唯一，因使用 id@站点地址 (eg. 114514@redrock.team) 作为唯一标识
 *
 * @author 寒雨
 * @since 2022/10/30 上午11:27
 */
object TablePost : LongIdTable("post") {

    // #1@anonymous.tree.hollow
    // 1@anonymous.tree.hollow
    // 为了保证匿名，只存储用户id+域名加盐(uuid)后的md5散列值，作为用户在当前帖子的唯一标识
    val senderMd5 = varchar("sender_md5", 32)
    // 当前帖子uuid - 通用盐值
    val uuid = uuid("uuid").uniqueIndex()
    // 来源 例: anonymous.tree.hollow
    val originSite = varchar("origin_site", 128)
    // 来源名称 例: 重庆邮电大学
    val originName = varchar("origin_name", 128)
    // 时间
    val postTime = long("post_time")
    // 帖子内容
    val content = text("content")
    // 图片url
    val image = varchar("image", 256).nullable()
    // tags 用,隔开
    val tags = text("tags").default("")
    val vote = optReference("vote", TableVote)

    init {
        // 给tags加上全文索引，提升搜索效率
        index(
            indexType = "FULLTEXT",
            columns = arrayOf(tags)
        )
    }
}

class PostEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PostEntity>(TablePost)

    var senderMd5 by TablePost.senderMd5
    var uuid by TablePost.uuid
    var originSite by TablePost.originSite
    var originName by TablePost.originName
    var postTime by TablePost.postTime
    var content by TablePost.content
    var image by TablePost.image
    var tags by TablePost.tags
    var vote by VoteEntity optionalReferencedOn TablePost.vote

    fun dto(): PostDto {
        return transaction {
            PostDto(
                id = this@PostEntity.id.value,
                senderMd5 = senderMd5,
                originSite = originSite,
                originName = originName,
                postTime = postTime,
                content = content,
                image = image,
                tags = tags.split(","),
                vote = vote?.dto()
            )
        }
    }
}