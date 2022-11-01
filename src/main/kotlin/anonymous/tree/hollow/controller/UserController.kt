package anonymous.tree.hollow.controller

import anonymous.tree.hollow.database.dto.ResponseDto
import anonymous.tree.hollow.database.service.UserService
import anonymous.tree.hollow.database.service.VerifyService
import anonymous.tree.hollow.utils.isAvailablePassword
import anonymous.tree.hollow.utils.isEmail
import anonymous.tree.hollow.utils.isStuEmail
import anonymous.tree.hollow.utils.randomVerifyCode
import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * anonymous.tree.hollow.controller.AuthController.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/28 下午4:41
 */
@RestController
@RequestMapping("/user/")
@CrossOrigin
class UserController(
    private val userService: UserService,
    private val verifyService: VerifyService) {

    /**
     * 注册
     *
     * @param email
     * @param password
     * @param verifyCode
     * @return
     */
    @PostMapping("register")
    fun ctrlRegister(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String,
        @RequestParam("verifyCode") verifyCode: String
    ): ResponseDto<String> {
        if (!email.isEmail()) {
            return ResponseDto.builder<String>()
                .message("邮箱地址不合法")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        if (!password.isAvailablePassword()) {
            return ResponseDto.builder<String>()
                .message("密码强度不足或不规范")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        if (!verifyService.verify(email, verifyCode)) {
            return ResponseDto.builder<String>()
                .message("验证码已过期")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        val userId = userService.register(email, password)
        // 帐号是否已经存在
        if (userId == -1L) {
            return ResponseDto.builder<String>()
                .message("这个邮箱已经注册过了")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        StpUtil.login(userId)
        return if (email.isStuEmail()) {
            ResponseDto.builder<String>()
                .message("是学生邮箱，无需验证")
                .status(HttpStatus.ACCEPTED)
                .build()
        } else {
            ResponseDto.builder<String>()
                .message("非学生邮箱，需要上传学生证照片进行验证")
                .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
                .build()
        }
    }

    /**
     * 登录
     *
     * @param email
     * @param password
     * @return
     */
    @PostMapping("login")
    fun ctrlLogin(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String
    ): ResponseDto<String> {
        val userId = userService.login(email, password)
        return if (userId != -1L) {
            StpUtil.login(userId)
            ResponseDto.ok()
        } else {
            ResponseDto.builder<String>()
                .message("帐号或密码错误")
                .status(HttpStatus.FORBIDDEN)
                .build()
        }
    }

    /**
     * 忘记密码
     *
     * @param email
     * @param verifyCode
     * @param newPassword
     * @return
     */
    @PostMapping("forgetpass")
    fun ctrlForgetPass(
        @RequestParam("email") email: String,
        @RequestParam("verifyCode") verifyCode: String,
        @RequestParam("newPassword") newPassword: String
    ): ResponseDto<String> {
        if (!verifyService.verify(email, verifyCode)) {
            return ResponseDto.builder<String>()
                .message("验证码不正确")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        if (!userService.changePassword(email, newPassword)) {
            return ResponseDto.builder<String>()
                .message("修改失败")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        return ResponseDto.ok()
    }

    /**
     * 修改密码
     *
     * @return
     */
    @SaCheckLogin
    @PostMapping("changepass")
    fun ctrlChangePass(
        @RequestParam("oldPassword") oldPassword: String,
        @RequestParam("newPassword") newPassword: String
    ): ResponseDto<String> {
        val userId = StpUtil.getLoginIdAsLong()
        if (!userService.changePassword(userId, oldPassword, newPassword)) {
            return ResponseDto.builder<String>()
                .message("老密码不正确")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        return ResponseDto.ok()
    }

    /**
     * 退出登录
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("logout")
    fun ctrlLogout(): ResponseDto<String> {
        val userId = StpUtil.getLoginIdAsLong()
        StpUtil.kickout(userId)
        return ResponseDto.ok()
    }
}