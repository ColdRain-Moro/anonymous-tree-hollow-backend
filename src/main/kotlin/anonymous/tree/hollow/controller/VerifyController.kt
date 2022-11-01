package anonymous.tree.hollow.controller

import anonymous.tree.hollow.database.dto.ResponseDto
import anonymous.tree.hollow.database.dto.VerifyRequestDto
import anonymous.tree.hollow.database.service.VerifyService
import anonymous.tree.hollow.user.UserType
import anonymous.tree.hollow.utils.isEmail
import anonymous.tree.hollow.utils.randomVerifyCode
import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * anonymous.tree.hollow.controller.UserVerifyController.kt
 * AnonymousTreeHollow
 * 审核学生卡相关逻辑
 *
 * @author 寒雨
 * @since 2022/10/31 下午11:41
 */
@RestController("/verify/")
class VerifyController(private val verifyService: VerifyService) {
    /**
     * 发送验证码
     *
     * @param email
     * @return
     */
    @PostMapping("send")
    fun ctrlVerify(
        @RequestParam("email") email: String
    ): ResponseDto<String> {
        if (!email.isEmail()) {
            return ResponseDto.builder<String>()
                .message("邮箱地址不合法")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        verifyService.sendVerifyCode(email, randomVerifyCode())
        return ResponseDto.builder<String>()
            .message("验证码已发送")
            .build()
    }

    /**
     * 已经发送请求，但未审核(0) / 未发送请求(1) / 请求已通过(2)
     */
    @SaCheckLogin
    @GetMapping("check")
    fun ctrlCheckStatus(): ResponseDto<Int> {
        if (!StpUtil.getRoleList().contains(UserType.UNAUTHORIZED.token)) {
            return ResponseDto.builder<Int>()
                .body(2)
                .build()
        }
        val email = StpUtil.getLoginId().toString()
        return ResponseDto.builder<Int>()
            .body(if (verifyService.hasRequested(email)) 0 else 1)
            .build()
    }


    @SaCheckLogin
    @SaCheckRole("unauthorized")
    @PutMapping("upgradeRequest")
    fun ctrlUpgradeRequest(
        @RequestParam("image") image: MultipartFile
    ): ResponseDto<String> {
        val email = StpUtil.getLoginId().toString()
        // 预检一下文件大小，大于10MB的情况下直接拒绝，防止OOM
        if (image.size > 10 * 1024 * 1024) {
            return ResponseDto.builder<String>()
                .status(HttpStatus.BAD_REQUEST)
                .message("上传照片大小不能超过10MB")
                .build()
        }
        verifyService.sendRequest(email, image)
        return ResponseDto.ok()
    }

    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("upgradeRequest")
    fun ctrlAccept(@RequestParam("id") id: Long): ResponseDto<String> {
        verifyService.acceptRequest(id)
        return ResponseDto.ok()
    }

    @SaCheckLogin
    @SaCheckRole("admin")
    @DeleteMapping("upgradeRequest")
    fun ctrlReject(@RequestParam("id") id: Long): ResponseDto<String> {
        verifyService.rejectRequest(id)
        return ResponseDto.ok()
    }

    @SaCheckLogin
    @SaCheckRole("admin")
    @GetMapping("upgradeRequest")
    fun ctrlGetRequestList(
        @RequestParam("offset", defaultValue = "0") offset: Long,
        @RequestParam("limit", defaultValue = "20") limit: Int
    ): ResponseDto<List<VerifyRequestDto>> {
        return ResponseDto.builder<List<VerifyRequestDto>>()
            .body(verifyService.getRequests(offset, limit))
            .build()
    }
}