package dev.erickvieira.ppcc.service.banking.integrated.balance

import com.github.database.rider.core.api.configuration.DBUnit
import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.junit5.api.DBRider
import dev.erickvieira.ppcc.service.banking.integrated.BankingServiceIntegratedTests
import dev.erickvieira.ppcc.service.banking.integrated.PostgresContainerSetup
import io.restassured.RestAssured
import org.approvaltests.Approvals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DBRider
@ActiveProfiles("test")
@TestPropertySource(value = ["classpath:application-test.properties"])
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, caseSensitiveTableNames = true, cacheConnection = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [PostgresContainerSetup::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class BalanceGetIT : BankingServiceIntegratedTests() {

    @LocalServerPort
    private val port: Int = 0

    @BeforeAll
    @DataSet("initial-state.yml")
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/v1/wallet"
    }

    @Test
    fun `balance - must calculate the given wallet balance`() {
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59/balance",
        ).asString()
        Approvals.verifyJson(responseBody)
    }

    @Test
    fun `balance - must reject with error wallet_not_found`() {
        val responseBody = makeRequest(
            path = "/4ef996c1-d1e2-530f-b142-75f6ce970c58/balance",
            statusCode = HttpStatus.NOT_FOUND
        ).asString()
        Approvals.verifyJson(responseBody)
    }

}