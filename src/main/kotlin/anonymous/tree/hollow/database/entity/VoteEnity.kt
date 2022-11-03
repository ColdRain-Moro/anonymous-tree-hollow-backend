package anonymous.tree.hollow.database.entity

import anonymous.tree.hollow.database.dto.VoteDto
import anonymous.tree.hollow.utils.repeatMap
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * anonymous.tree.hollow.database.entity.VoteEntity.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/2 下午6:58
 */
object TableVote : LongIdTable() {
    val post = reference("post_id", TablePost).uniqueIndex()
    val content = text("content")
    val optionA = text("option_a")
    val optionB = text("option_b").nullable()
    val optionC = text("option_c").nullable()
    val optionD = text("option_d").nullable()
}

class VoteEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VoteEntity>(TableVote)

    var post by PostEntity referencedOn TableVote.post
    var content by TableVote.content
    var optionA by TableVote.optionA
    var optionB by TableVote.optionB
    var optionC by TableVote.optionC
    var optionD by TableVote.optionD

    fun dto(): VoteDto {
        return transaction {
            val results = repeatMap(4) {
                VoteRecordEntity.find { TableVoteRecord.vote eq this@VoteEntity.id and (TableVoteRecord.option eq it + 1) }
                    .count()
            }
            VoteDto(
                this@VoteEntity.id.value,
                post.dto(),
                content,
                optionA,
                optionB,
                optionC,
                optionD,
                results
            )
        }
    }
}