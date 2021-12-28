package dev.erickvieira.ppcc.service.banking.config

import org.openapitools.jackson.nullable.JsonNullableModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JacksonConfig {

    @Bean
    open fun jsonNullableModule() = JsonNullableModule()

}