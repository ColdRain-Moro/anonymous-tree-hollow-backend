package anonymous.tree.hollow.handler

import anonymous.tree.hollow.database.dto.ResponseDto
import anonymous.tree.hollow.user.UserType
import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.NotPermissionException
import cn.dev33.satoken.exception.NotRoleException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception

/**
 * anonymous.tree.hollow.handler.GlobalExceptionHandler.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/28 下午4:21
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun handlerException(exception: Exception): ResponseEntity<ResponseDto<String>> {
        return when (exception) {
            is NotLoginException -> ResponseEntity
                .ok(
                    ResponseDto.builder<String>()
                        .message("未登录")
                        .status(HttpStatus.UNAUTHORIZED)
                        .build()
                )
            is NotRoleException -> {

                ResponseEntity.ok(
                    ResponseDto.builder<String>()
                        .message(if (exception.role == UserType.UNAUTHORIZED.token) "尚未通过验证" else "无权访问")
                        .status(HttpStatus.UNAUTHORIZED)
                        .build()
                )
            }
            is NotPermissionException -> {
                ResponseEntity.ok(
                    ResponseDto.builder<String>()
                        .message("权限不足")
                        .status(HttpStatus.UNAUTHORIZED)
                        .build()
                )
            }
            else -> {
                exception.printStackTrace()
                ResponseEntity.internalServerError().build()
            }
        }
    }
}