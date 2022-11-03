package anonymous.tree.hollow.database.service

import anonymous.tree.hollow.database.dto.CommentDto
import anonymous.tree.hollow.database.dto.PostDto
import anonymous.tree.hollow.database.dto.VoteDto
import anonymous.tree.hollow.database.dto.request.VoteInfo
import anonymous.tree.hollow.database.dto.response.QueryPostResponse
import anonymous.tree.hollow.database.entity.*
import anonymous.tree.hollow.service.CDNService
import anonymous.tree.hollow.utils.md5
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

/**
 * anonymous.tree.hollow.database.service.PostService.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/31 下午12:54
 */
@Service
class PostService(private val cdnService: CDNService) {

    @Value("\${anonymous-tree-hollow.site-name}")
    private lateinit var siteName: String
    @Value("\${anonymous-tree-hollow.site-address}")
    private lateinit var siteAddress: String
    @Value("\${anonymous-tree-hollow.cdn.image-bed-scope}")
    private lateinit var imgBedScope: String

    fun putPost(id: Long, content: String, image: MultipartFile?, tags: String, voteInfo: VoteInfo?): PostDto {
        return transaction {
            val uuid = UUID.randomUUID()
            val imageUrl = image?.let {
                val suffix = image.originalFilename?.split(".")?.last() ?: "jpg"
                cdnService.putObject(imgBedScope, "$uuid.$suffix", image.inputStream, image.size)
            }
            val user = UserEntity.findById(id) ?: error("")
            val post = PostEntity.new {
                this.uuid = uuid
                // 1@redrock.team加盐再md5散列
                senderMd5 = "${user.id}@$siteAddress$uuid".md5()
                originSite = siteAddress
                originName = siteName
                postTime = System.currentTimeMillis()
                this.content = content
                this.image = imageUrl
                this.tags = tags
            }
            val vote = voteInfo?.let {
                VoteEntity.new {
                    this.post = post
                    this.content = voteInfo.content
                    optionA = voteInfo.options[0]
                    optionB = voteInfo.options.getOrNull(1)
                    optionC = voteInfo.options.getOrNull(2)
                    optionD = voteInfo.options.getOrNull(3)
                }
            }
            post.vote = vote
            post.dto()
        }
    }

    fun deletePost(postId: Long): Boolean {
        return transaction {
            val post = PostEntity.findById(postId) ?: return@transaction false
            post.delete()
            true
        }
    }

    fun getPosts(limit: Int, offset: Long, filter: String, userId: Long): List<QueryPostResponse> {
        return transaction {
            val iter = if (filter.isEmpty()) {
                PostEntity.all()
            } else {
                PostEntity.find {
                    filter.split(",")
                        .map { TablePost.tags like "%$it%" }
                        .reduce<Op<Boolean>, Op<Boolean>> { likeEscapeOp, acc -> likeEscapeOp or acc }
                }
            }
            iter.limit(limit, offset)
                .orderBy(TablePost.postTime to SortOrder.DESC)
                .map {
                    it to if (it.vote != null) {
                        VoteRecordEntity.find {
                            TableVoteRecord.vote eq it.vote!!.id and
                                    (TableVoteRecord.senderMd5 eq "${userId}@$siteAddress".md5())
                        }.firstOrNull()?.option ?: 0
                    } else {
                        0
                    }
                }
                .map { (postEntity, option) -> QueryPostResponse(postEntity.dto(), option) }
        }
    }

    fun searchPosts(limit: Int, offset: Long, key: String): List<PostDto> {
        return transaction {
            PostEntity.find { TablePost.content like "%$key%" }
                .limit(limit, offset)
                .orderBy(TablePost.postTime to SortOrder.DESC)
                .map { it.dto() }
        }
    }

    fun getComments(postId: Long, limit: Int, offset: Long): List<CommentDto> {
        return transaction {
            CommentEntity.find { TableComment.post eq postId }
                .limit(limit, offset)
                .orderBy(TablePost.postTime to SortOrder.DESC)
                .map { it.dto() }
        }
    }

    fun putComment(postId: Long, userId: Long, content: String, image: MultipartFile?, replyId: Long?): Boolean {
        val uuid = UUID.randomUUID()
        val imageUrl = image?.let {
            val suffix = image.originalFilename?.split(".")?.last() ?: "jpg"
            cdnService.putObject(imgBedScope, "$uuid.$suffix", image.inputStream, image.size)
        }
        return transaction {
            val reply = replyId?.let { CommentEntity.findById(it) }
            val post = PostEntity.findById(postId) ?: return@transaction false
            CommentEntity.new {
                senderMd5 = "${userId}@$siteAddress${post.uuid}".md5()
                this.post = post
                postTime = System.currentTimeMillis()
                originSite = siteAddress
                originName = siteName
                this.content = content
                this.reply = reply
                this.image = imageUrl
            }
            true
        }
    }

    fun vote(userId: Long, voteId: Long, option: Int): Boolean {
        return transaction {
            val vote = VoteEntity.findById(voteId) ?: return@transaction false
            if (!VoteRecordEntity.find {
                TableVoteRecord.vote eq voteId and
                        (TableVoteRecord.senderMd5 eq "${userId}@$siteAddress".md5())
            }.empty()) return@transaction false
            VoteRecordEntity.new {
                senderMd5 = "${userId}@$siteAddress".md5()
                this.option = option
                this.vote = vote
            }
            true
        }
    }
}