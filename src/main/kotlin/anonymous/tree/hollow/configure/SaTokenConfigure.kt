package anonymous.tree.hollow.configure

import anonymous.tree.hollow.database.service.UserService
import cn.dev33.satoken.interceptor.SaInterceptor
import cn.dev33.satoken.jwt.StpLogicJwtForSimple
import cn.dev33.satoken.stp.StpInterface
import cn.dev33.satoken.stp.StpLogic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * anonymous.tree.hollow.configure.SaTokenConfig.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/28 下午11:39
 */
@Configuration
@EnableWebMvc
class SaTokenConfigure : WebMvcConfigurer {
    @Bean
    fun getStpLogicJwt(): StpLogic? {
        return StpLogicJwtForSimple()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(SaInterceptor()).addPathPatterns("/**")
    }
}

@Component
class StpInterfaceImpl(private val authService: UserService) : StpInterface {
    override fun getPermissionList(loginId: Any, loginType: String): MutableList<String> {
        return mutableListOf()
    }

    override fun getRoleList(loginId: Any, loginType: String): MutableList<String> {
        val list = mutableListOf<String>()
        authService.getUserType(loginId.toString())?.let { list.add(it.token) }
        return list
    }
}