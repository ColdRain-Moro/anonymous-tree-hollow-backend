package anonymous.tree.hollow.database.dto

/**
 * anonymous.tree.hollow.database.dto.UserDto.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/1 上午12:37
 */
data class UserDto(
    val id: Long,
    val email: String,
    val idCardImgUrl: String? = null,
    val type: String,
    val join: Long
)