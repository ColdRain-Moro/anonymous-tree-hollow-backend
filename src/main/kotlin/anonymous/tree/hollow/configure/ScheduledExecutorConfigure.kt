package anonymous.tree.hollow.configure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * anonymous.tree.hollow.configure.ScheduledExecutorConfigure.kt
 * AnonymousTreeHollow
 *
 * @author 寒雨
 * @since 2022/10/29 下午2:42
 */
@Configuration
class ScheduledExecutorConfigure {
    @Bean
    fun provide(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(20)
    }
}