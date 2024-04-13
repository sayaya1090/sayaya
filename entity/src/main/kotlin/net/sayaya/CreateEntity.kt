package net.sayaya

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CreateEntity {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<CreateEntity>(*args)
        }
    }
}