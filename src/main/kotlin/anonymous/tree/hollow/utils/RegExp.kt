package anonymous.tree.hollow.utils

import org.springframework.beans.factory.annotation.Value

/**
 * anonymous.tree.hollow.utils.Strings.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/29 下午1:39
 */
private val emailReg = "^[0-9a-z][0-9a-z-_.]{0,35}@([0-9a-z][0-9a-z-]{0,35}[0-9a-z]\\.){1,5}[a-z]{2,4}\$".toRegex()

@Value("\${anonymous-tree-hollow.stu-email-suffix}")
private lateinit var stuEmailSuffix: String

fun String.isEmail() = emailReg.matches(this)

fun String.isStuEmail() = isEmail() && endsWith(stuEmailSuffix)



