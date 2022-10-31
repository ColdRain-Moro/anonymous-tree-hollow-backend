package anonymous.tree.hollow.user

/**
 * anonymous.tree.hollow.user.User.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/29 下午8:33
 */
enum class UserType(val code: Int, val token: String) {
    // 未激活用户
    UNAUTHORIZED(0, "unauthorized"),
    // 普通用户
    USER(1, "user"),
    // 管理员
    ADMIN(2, "admin");

    companion object {

        fun fromCode(code: Int): UserType {
            return when (code) {
                0 -> UNAUTHORIZED
                1 -> USER
                2 -> ADMIN
                else -> error("")
            }
        }

        fun fromToken(token: String): UserType {
            return when (token) {
                UNAUTHORIZED.token -> UNAUTHORIZED
                USER.token -> USER
                ADMIN.token -> ADMIN
                else -> error("")
            }
        }
    }
}