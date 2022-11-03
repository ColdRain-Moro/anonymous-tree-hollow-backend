package anonymous.tree.hollow.utils

/**
 * anonymous.tree.hollow.utils.Collections.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/11/2 下午9:20
 */
fun <T> repeatMap(size: Int, mapper: (index: Int) -> T): List<T> {
    val list = mutableListOf<T>()
    repeat(size) {
        list.add(mapper(it))
    }
    return list
}