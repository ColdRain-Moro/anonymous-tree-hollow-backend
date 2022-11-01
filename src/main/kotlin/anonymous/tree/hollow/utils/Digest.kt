package anonymous.tree.hollow.utils

import cn.hutool.crypto.digest.MD5
import java.security.MessageDigest

/**
 * anonymous.tree.hollow.utils.Digest.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/29 下午4:31
 */
fun String.md5(): String {
    return MD5.create().digestHex16(this)
}