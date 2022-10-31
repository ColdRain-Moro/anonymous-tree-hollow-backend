package anonymous.tree.hollow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@Configuration
@EnableCaching
@EnableAsync
class AnonymousTreeHollowApplication

fun main(args: Array<String>) {
    runApplication<AnonymousTreeHollowApplication>(*args)
}
