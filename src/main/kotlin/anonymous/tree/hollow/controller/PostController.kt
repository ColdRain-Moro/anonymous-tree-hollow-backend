package anonymous.tree.hollow.controller

import anonymous.tree.hollow.database.dto.PostDto
import anonymous.tree.hollow.database.dto.ResponseDto
import anonymous.tree.hollow.database.service.PostService
import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
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
class PostController(private val postService: PostService) {

    @PutMapping
    fun ctrlPutPost(
        @RequestParam("content") content: String,
        @RequestParam("image", required = false) image: MultipartFile?,
        @RequestParam("tags") tags: String
    ): ResponseDto<PostDto> {
        if (content.length > 1000) {
            return ResponseDto.builder<PostDto>()
                .status(HttpStatus.BAD_REQUEST)
                .message("文字内容太长，单个帖子/评论内容不能超过1000个字符捏")
                .build()
        }
        if (image != null && image.size > 10 * 1024 * 1024) {
            return ResponseDto.builder<PostDto>()
                .status(HttpStatus.BAD_REQUEST)
                .message("图片太大啦，图片大小不能超过10MB")
                .build()
        }
        val post = postService.putPost(StpUtil.getLoginIdAsLong(), content, image, tags)
        return ResponseDto.builder<PostDto>()
            .body(post)
            .build()
    }

    @DeleteMapping
    @SaCheckRole("admin")
    fun ctrlDeletePost(
        @RequestParam("postId") postId: Long
    ): ResponseDto<String> {
        if (!postService.deletePost(postId)) {
            return ResponseDto.builder<String>()
                .status(HttpStatus.BAD_REQUEST)
                .message("找不到目标帖子")
                .build()
        }
        return ResponseDto.ok()
    }

    // 登录才可查看 这样最安全
    // 分页查找
    @GetMapping
    fun ctrlGetPosts(
        @RequestParam("offset", defaultValue = "0", required = false) offset: Long,
        @RequestParam("limit", defaultValue = "20", required = false) limit: Int,
        @RequestParam("filter", defaultValue = "", required = false) filter: String
    ): ResponseDto<List<PostDto>> {
        return ResponseDto.builder<List<PostDto>>()
            .body(postService.getPosts(limit, offset, filter))
            .build()
    }

    @PostMapping("/search")
    fun ctrlSearchPost(
        @RequestParam("offset", defaultValue = "0", required = false) offset: Long,
        @RequestParam("limit", defaultValue = "20", required = false) limit: Int,
        @RequestParam("key") key: String
    ): ResponseDto<List<PostDto>> {
        return ResponseDto.builder<List<PostDto>>()
            .body(postService.searchPosts(limit, offset, key))
            .build()
    }

    @GetMapping("/comment")
    fun ctrlGetComments(
        @RequestParam("offset", defaultValue = "0", required = false) offset: Int,
        @RequestParam("limit", defaultValue = "20", required = false) limit: Int
    ) {

    }

    @PutMapping("/comment")
    fun ctrlPutComment() {

    }
}