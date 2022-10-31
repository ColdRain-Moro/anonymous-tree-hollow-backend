package anonymous.tree.hollow.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * anonymous.tree.hollow.controller.PostController.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/31 下午4:26
 */
@SaCheckLogin
@SaCheckRole("user", "admin")
@RestController("/post/")
class PostController {

    @Value("\${anonymous-tree-hollow.site-name}")
    private lateinit var siteName: String
    @Value("\${anonymous-tree-hollow.site-address}")
    private lateinit var siteAddress: String

    @PutMapping
    fun ctrlPutPost(
        @RequestParam("content") content: String,
        @RequestParam("image", required = false) image: MultipartFile?,
    ) {

    }

    @DeleteMapping
    @SaCheckRole("admin")
    fun ctrlDeletePost() {

    }

    // 登录才可查看 这样最安全
    // 分页查找
    @GetMapping
    fun ctrlGetPosts(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int
    ) {

    }

    @PostMapping("/search")
    fun ctrlSearchPost() {

    }

    @GetMapping("/comment")
    fun ctrlGetComments() {

    }

    @PutMapping("/comment")
    fun ctrlPutComment() {

    }
}