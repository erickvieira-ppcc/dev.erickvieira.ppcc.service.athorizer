package dev.erickvieira.ppcc.service.banking.integrated

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.junit.jupiter.Container

class PostgresContainerSetup : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        postgres.portBindings = listOf("5432:5432")
        postgres.start()
        postgres.waitingFor(LogMessageWaitStrategy().withRegEx("Container postgres:13.2-alpine started"))
    }

    companion object {
        @Container
        private val postgres: GenericContainer<*> =  PostgreSQLContainer<Nothing>("postgres:13.2-alpine")
            .apply {
                withExposedPorts(5432)
                withDatabaseName("banking_db")
                withUsername("ppcc")
                withPassword("ppcc")
            }
    }
}
