package anonymous.tree.hollow.database.service

import anonymous.tree.hollow.database.dto.PostDto
import anonymous.tree.hollow.database.entity.PostEntity
import anonymous.tree.hollow.database.entity.TablePost
import anonymous.tree.hollow.database.entity.UserEntity
import anonymous.tree.hollow.service.CDNService
import anonymous.tree.hollow.utils.md5
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
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

    fun putPost(id: Long, content: String, image: MultipartFile?, tags: String): PostDto {
        return transaction {
            val uuid = UUID.randomUUID()
            val imageUrl = image?.let {
                val suffix = image.originalFilename?.split(".")?.last() ?: "jpg"
                cdnService.putObject(imgBedScope, "$uuid.$suffix", image.inputStream, image.size)
            }
            val user = UserEntity.findById(id) ?: error("")
            PostEntity.new {
                this.uuid = uuid
                // 1@redrock.team加盐再md5散列
                senderMd5 = "${user.id}@$siteAddress$uuid".md5()
                originSite = siteAddress
                originName = siteName
                postTime = System.currentTimeMillis()
                this.content = content
                this.image = imageUrl
                this.tags = tags
            }.dto()
        }
    }

    fun deletePost(postId: Long): Boolean {
        return transaction {
            val post = PostEntity.findById(postId) ?: return@transaction false
            post.delete()
            true
        }
    }

    fun getPosts(limit: Int, offset: Long, filter: String): List<PostDto> {
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
                .map { it.dto() }
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
}