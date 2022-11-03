package anonymous.tree.hollow.controller

import anonymous.tree.hollow.database.dto.CommentDto
import anonymous.tree.hollow.database.dto.PostDto
import anonymous.tree.hollow.database.dto.request.VoteInfo
import anonymous.tree.hollow.database.dto.response.QueryPostResponse
import anonymous.tree.hollow.database.dto.response.ResponseDto
import anonymous.tree.hollow.database.service.PostService
import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    /**
     * 发帖
     *
     * @param content
     * @param image
     * @param tags
     * @param voteInfo json格式的 VoteInfo
     * @return
     */
    @PutMapping
    fun ctrlPutPost(
        @RequestParam("content") content: String,
        @RequestParam("image", required = false) image: MultipartFile?,
        @RequestParam("tags", required = false, defaultValue = "") tags: String,
        @RequestParam("voteInfo", required = false) voteInfo: String?
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
        val voteInfoObj = kotlin.runCatching { voteInfo?.let { objectMapper.readValue<VoteInfo>(it) } }
            .onFailure {
                return ResponseDto.builder<PostDto>()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("voteInfo json数据解析异常")
                    .build()
            }
            .getOrThrow()
        val post = kotlin.runCatching { postService.putPost(StpUtil.getLoginIdAsLong(), content, image, tags, voteInfoObj) }
            .onFailure {
                return ResponseDto.builder<PostDto>()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("voteInfoObj参数异常")
                    .build()
            }
            .getOrThrow()
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
    ): ResponseDto<List<QueryPostResponse>> {
        return ResponseDto.builder<List<QueryPostResponse>>()
            .body(postService.getPosts(limit, offset, filter, StpUtil.getLoginIdAsLong()))
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
        @RequestParam("postId") postId: Long,
        @RequestParam("offset", defaultValue = "0", required = false) offset: Long,
        @RequestParam("limit", defaultValue = "20", required = false) limit: Int
    ): ResponseDto<List<CommentDto>> {
        val comments = postService.getComments(postId, limit, offset)
        return ResponseDto.builder<List<CommentDto>>()
            .body(comments)
            .build()
    }

    // 要做分布式的话这里要改一改，加一个只有服务器之间能互相访问的接口
    // 并且兼容不同站点标识
    @PutMapping("/comment")
    fun ctrlPutComment(
        @RequestParam("postId") postId: Long,
        @RequestParam("content") content: String,
        @RequestParam("image", required = false) image: MultipartFile?,
        @RequestParam("reply", required = false) reply: Long?
    ): ResponseDto<String> {
        if (!postService.putComment(postId, StpUtil.getLoginIdAsLong(), content, image, reply)) {
            return ResponseDto.builder<String>()
                .status(HttpStatus.BAD_REQUEST)
                .message("不存在的帖子")
                .build()
        }
        return ResponseDto.ok()
    }

    @PutMapping("/vote")
    fun ctrlVote(
        @RequestParam("voteId") voteId: Long,
        @RequestParam("option") option: Int
    ): ResponseDto<String> {
        if (!postService.vote(StpUtil.getLoginIdAsLong(), voteId, option)) {
            return ResponseDto.builder<String>()
                .status(HttpStatus.BAD_REQUEST)
                .message("投票不存在或已经投过票")
                .build()
        }
        return ResponseDto.ok()
    }
}