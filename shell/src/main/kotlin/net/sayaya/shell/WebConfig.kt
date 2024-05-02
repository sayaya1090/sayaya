package net.sayaya.shell

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebConfig {
    @Bean fun client(): WebClient = WebClient.builder().build()
}