package anonymous.tree.hollow.database.dto.response

import org.springframework.http.HttpStatus

/**
 * anonymous.tree.hollow.database.dto.response.ResponseDto.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/29 下午12:06
 */
data class ResponseDto<T>(
    val status: Int,
    val message: String,
    val data: T?
) {
    companion object {
        fun <T> builder() = ResponseDtoBuilder<T>()

        fun <T> ok() = builder<T>().build()
    }
}

class ResponseDtoBuilder<T> {
    private var status: HttpStatus = HttpStatus.OK
    private var message: String = ""
    private var data: T? = null

    fun status(status: HttpStatus): ResponseDtoBuilder<T> {
        this.status = status
        return this
    }

    fun message(message: String): ResponseDtoBuilder<T> {
        this.message = message
        return this
    }

    fun body(body: T): ResponseDtoBuilder<T> {
        this.data = body
        return this
    }

    fun build(): ResponseDto<T> {
        return ResponseDto(status.value(), message, data)
    }
}