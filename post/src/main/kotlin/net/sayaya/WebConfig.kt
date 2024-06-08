package net.sayaya

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebConfig(private val objectMapper: ObjectMapper) {
    @Bean fun client(): WebClient = WebClient.builder().codecs { config ->
        config.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
        config.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
    }.build()
}