package anonymous.tree.hollow.database.dto

/**
 * anonymous.tree.hollow.database.dto.PostDto.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/1 下午11:55
 */
data class PostDto(
    val id: Long,
    val senderMd5: String,
    val originSite: String,
    val originName: String,
    val postTime: Long,
    val content: String,
    val image: String?,
    val tags: List<String>,
    val vote: VoteDto?
)