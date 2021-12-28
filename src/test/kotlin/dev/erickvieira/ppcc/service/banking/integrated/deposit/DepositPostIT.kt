package dev.erickvieira.ppcc.service.banking.integrated.deposit

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
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.jdbc.JdbcTestUtils
import java.util.*

@DBRider
@ActiveProfiles("test")
@TestPropertySource(value = ["classpath:application-test.properties"])
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, caseSensitiveTableNames = true, cacheConnection = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [PostgresContainerSetup::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class DepositPostIT : BankingServiceIntegratedTests() {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private val jdbcTemplate: JdbcTemplate? = null

    @BeforeAll
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/v1/banking/deposit"
    }

    @AfterEach
    fun tearDown() {
        jdbcTemplate?.apply {
            JdbcTestUtils.deleteFromTables(this, "tb_transaction", "tb_wallet")
        }
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "deposit/scenario-approved.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `deposit - must credit the given wallet`() {
        val payload = generateTransactionDTO(
            walletId = UUID.fromString("a9a03905-d962-592c-aad0-f7555d81fe59"),
            value = 20.50
        )
        val responseBody = makeRequest(payload = payload).asString("timestamp", "id")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "deposit/scenario-approved.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `deposit - must credit the default wallet of the given user`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("9c05df92-4044-54ee-964c-ab5aee0b4f57"),
            value = 20.50
        )
        val responseBody = makeRequest(payload = payload).asString("timestamp", "id")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "initial-state.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `deposit - must decline with error declined_by_wallet_not_found`() {
        val payload = generateTransactionDTO(
            userId = UUID.randomUUID(),
            value = 20.50
        )
        val responseBody = makeRequest(
            payload = payload,
            statusCode = HttpStatus.NOT_FOUND
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "initial-state.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `deposit - must decline with error declined_by_invalid_value`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("9c05df92-4044-54ee-964c-ab5aee0b4f57"),
            value = 0.00
        )
        val responseBody = makeRequest(
            payload = payload,
            statusCode = HttpStatus.BAD_REQUEST
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "initial-state.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `deposit - must decline with error declined_by_null_payload`() {
        val payload = null
        val responseBody = makeRequest(
            payload = payload,
            statusCode = HttpStatus.BAD_REQUEST
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("deposit/scenario-accept-deposit-false.yml")
    @ExpectedDataSet(
        "deposit/scenario-declined-forbidden-operation.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `deposit - must decline with error declined_by_forbidden_operation`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("9c05df92-4044-54ee-964c-ab5aee0b4f57"),
            value = 10.00
        )
        val responseBody = makeRequest(
            payload = payload
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("scenario-is-active-false.yml")
    @ExpectedDataSet(
        "deposit/scenario-declined-forbidden-operation.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `deposit - must decline with error declined_by_forbidden_operation - alt`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("9c05df92-4044-54ee-964c-ab5aee0b4f57"),
            value = 10.00
        )
        val responseBody = makeRequest(
            payload = payload
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

}