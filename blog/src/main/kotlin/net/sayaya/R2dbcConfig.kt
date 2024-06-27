package net.sayaya

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.CustomConversions
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
class R2dbcConfig {
    @Bean fun r2dbcCustomConversions(): R2dbcCustomConversions = R2dbcCustomConversions(CustomConversions.StoreConversions.NONE,
        listOf(LocalDateTimeToLongConverter(), LongToLocalDateTimeConverter()))

    @ReadingConverter
    class LocalDateTimeToLongConverter : Converter<LocalDateTime, Long> {
        override fun convert(source: LocalDateTime): Long = source.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    @WritingConverter
    class LongToLocalDateTimeConverter : Converter<Long, LocalDateTime> {
        override fun convert(source: Long): LocalDateTime? = LocalDateTime.ofInstant(Instant.ofEpochMilli(source), ZoneOffset.UTC)
    }
}