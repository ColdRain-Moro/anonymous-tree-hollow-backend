package anonymous.tree.hollow.database.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * anonymous.tree.hollow.database.entity.VoteRecordEntity.kt
 * AnonymousTreeHollow
 *
 * 这个也是各存各的，每个后端只存自己本端发出的帖子
 *
 * @author 寒雨
 * @since 2022/11/2 下午7:12
 */
object TableVoteRecord : LongIdTable() {
    val vote = reference("vote_id", TableVote)
    // 1 A 2 B 3 C 4 D
    val option = integer("option")
    // 不加盐了，直接md5
    val senderMd5 = varchar("sender_md5", 32)
}

class VoteRecordEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VoteRecordEntity>(TableVoteRecord)

    var vote by VoteEntity referencedOn TableVoteRecord.vote
    var option by TableVoteRecord.option
    var senderMd5 by TableVoteRecord.senderMd5

}