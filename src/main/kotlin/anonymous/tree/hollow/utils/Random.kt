package anonymous.tree.hollow.utils

import cn.hutool.core.lang.UUID
import kotlin.random.Random

/**
 * anonymous.tree.hollow.utils.Random.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/29 下午5:10
 */
/**
 * 取随机盐值
 *
 * @return
 */
fun randomSalt(): String {
    return UUID.randomUUID().toString()
}

/**
 * 随机验证码
 *
 * @return
 */
fun randomVerifyCode(): String {
    return Random.nextInt(10000, 99999).toString()
}