package dev.erickvieira.ppcc.service.banking.integrated.payment

import com.github.database.rider.core.api.configuration.DBUnit
import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.api.dataset.CompareOperation
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.core.api.dataset.ExpectedDataSet
import com.github.database.rider.junit5.api.DBRider
import dev.erickvieira.ppcc.service.banking.integrated.BankingServiceIntegratedTests
import dev.erickvieira.ppcc.service.banking.integrated.PostgresContainerSetup
import io.restassured.RestAssured
import org.approvaltests.Approvals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.jdbc.JdbcTestUtils
import java.util.*

@DBRider
@TestPropertySource(value = ["classpath:application.properties"])
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, caseSensitiveTableNames = false, cacheConnection = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [PostgresContainerSetup::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class PayPostIT : BankingServiceIntegratedTests() {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private val jdbcTemplate: JdbcTemplate? = null

    @BeforeAll
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/v1/banking/pay"
    }

    @AfterEach
    fun tearDown() {
        jdbcTemplate?.apply {
            JdbcTestUtils.deleteFromTables(this, "tb_wallet", "tb_transaction")
        }
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "payment/scenario-approved-with-wallet-id.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `pay - must debit the given wallet`() {
        val payload = generateTransactionDTO(
            walletId = UUID.fromString("a9a03905-d962-592c-aad0-f7555d81fe59"),
            value = 120.50
        )
        val responseBody = makeRequest(payload)
        Approvals.verifyJson(responseBody)
    }

}