package anonymous.tree.hollow.utils

/**
 * anonymous.tree.hollow.utils.Strings.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/29 下午7:11
 */
/**
 * 密码强度约束
 *
 * @return
 */
fun String.isAvailablePassword(): Boolean {
    return length >= 6
}

