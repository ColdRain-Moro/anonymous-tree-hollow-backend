package anonymous.tree.hollow.database.dto

/**
 * anonymous.tree.hollow.database.dto.VoteDto.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/2 下午8:04
 */
data class VoteDto(
    val id: Long,
    val postDto: PostDto,
    val content: String,
    val optionA: String,
    val optionB: String?,
    val optionC: String?,
    val optionD: String?,
    val results: List<Long>
)