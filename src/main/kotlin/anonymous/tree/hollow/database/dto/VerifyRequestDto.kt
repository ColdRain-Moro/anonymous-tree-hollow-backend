package anonymous.tree.hollow.database.dto

/**
 * anonymous.tree.hollow.database.dto.VerifyRequestDto.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/1 上午12:36
 */
data class VerifyRequestDto(
    val id: Long,
    val imageUrl: String,
    val user: UserDto,
    val time: Long
)
