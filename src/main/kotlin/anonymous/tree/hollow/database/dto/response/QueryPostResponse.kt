package anonymous.tree.hollow.database.dto.response

import anonymous.tree.hollow.database.dto.PostDto

/**
 * anonymous.tree.hollow.database.dto.response.QueryPostResponse.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/2 下午11:42
 */
data class QueryPostResponse(
    val post: PostDto,
    // 未投即0
    val votedOption: Int = 0
)