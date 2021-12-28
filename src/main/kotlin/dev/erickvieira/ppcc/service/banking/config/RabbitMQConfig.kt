package dev.erickvieira.ppcc.service.banking.config

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RabbitMQConfig {

    @Value("\${ppcc.walletqueue}")
    private lateinit var walletQueueName: String

    @Bean
    open fun walletQueue() = Queue(walletQueueName, false)

}