package anonymous.tree.hollow.database.dto.request

/**
 * anonymous.tree.hollow.database.dto.request.VoteInfo.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/2 下午8:51
 */
data class VoteInfo(
    val content: String,
    val options: List<String>
)