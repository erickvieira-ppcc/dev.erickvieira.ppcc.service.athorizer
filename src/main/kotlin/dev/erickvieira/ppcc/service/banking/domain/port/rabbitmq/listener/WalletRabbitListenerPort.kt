package dev.erickvieira.ppcc.service.banking.domain.port.rabbitmq.listener

import com.google.gson.Gson
import com.rabbitmq.client.Channel
import dev.erickvieira.ppcc.service.banking.domain.entity.Wallet
import dev.erickvieira.ppcc.service.banking.domain.repository.WalletRepository
import dev.erickvieira.ppcc.service.banking.extension.custom
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class WalletRabbitListenerPort(
    private val walletRepository: WalletRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(WalletRabbitListenerPort::class.java)

    private val gson = Gson()

    @RabbitListener(
        bindings = [QueueBinding(
            value = Queue(
                value = "\${ppcc.walletqueue}",
                durable = "false"
            ),
            exchange = Exchange(
                value = "\${ppcc.walletqueue}.exchange",
                durable = "false"
            ),
        )]
    )
    @RabbitHandler
    fun receive(@Payload message: String, channel: Channel, @Headers headers: Map<String, Any>) {
        try {
            logger.custom.info("fromRabbitMQ" to message)
            gson.fromJson(message, Wallet::class.java).let { walletRepository.save(it) }
            channel.basicAck(headers[AmqpHeaders.DELIVERY_TAG] as Long, false)
        } catch (e: Exception) {
            logger.custom.error(e.message)
        } catch (e: Error) {
            logger.custom.error(e.message)
        }
    }

}