package anonymous.tree.hollow.database.dto

/**
 * anonymous.tree.hollow.database.dto.CommentDto.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/2 上午10:36
 */
data class CommentDto(
    val id: Long,
    val senderMd5: String,
    val post: PostDto,
    val postTime: Long,
    val originSite: String,
    val originName: String,
    val content: String,
    val image: String?,
    val reply: CommentDto?
)