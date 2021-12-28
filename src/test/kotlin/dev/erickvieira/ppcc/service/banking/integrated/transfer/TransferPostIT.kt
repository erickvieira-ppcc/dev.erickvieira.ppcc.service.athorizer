package dev.erickvieira.ppcc.service.banking.integrated.transfer

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
class TransferPostIT : BankingServiceIntegratedTests() {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private val jdbcTemplate: JdbcTemplate? = null

    @BeforeAll
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/v1/banking/transfer"
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
        "bank-transfer/scenario-approved.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `transfer - must debit the given wallet`() {
        val payload = generateTransactionDTO(
            walletId = UUID.fromString("4622f0f7-7134-5301-a918-b5ba7ffd0ea3"),
            value = 20.50
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
            payload = payload
        ).asString("timestamp", "id")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "bank-transfer/scenario-approved.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `transfer - must debit the default wallet of the given user`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("f363e5e5-1d31-5f01-b58d-300501e9c4ff"),
            value = 20.50
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
            payload = payload
        ).asString("timestamp", "id")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "initial-state.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `transfer - must decline with error declined_by_wallet_not_found`() {
        val payload = generateTransactionDTO(
            userId = UUID.randomUUID(),
            value = 20.50
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
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
    fun `transfer - must decline with error declined_by_invalid_value`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("f363e5e5-1d31-5f01-b58d-300501e9c4ff"),
            value = 0.00
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
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
    fun `transfer - must decline with error declined_by_null_payload`() {
        val payload = null
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
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
    fun `transfer - must decline with error declined_by_insufficient_funds`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("f363e5e5-1d31-5f01-b58d-300501e9c4ff"),
            value = 1000.00
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
            payload = payload,
            statusCode = HttpStatus.BAD_REQUEST
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("scenario-min-balance-not-zero.yml")
    @ExpectedDataSet(
        "scenario-min-balance-not-zero.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `transfer - must decline with error declined_by_min_balance`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("f363e5e5-1d31-5f01-b58d-300501e9c4ff"),
            value = 60.00
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
            payload = payload,
            statusCode = HttpStatus.BAD_REQUEST
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("bank-transfer/scenario-accept-bank-transfer-false.yml")
    @ExpectedDataSet(
        "bank-transfer/scenario-accept-bank-transfer-false.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `transfer - must decline with error declined_by_forbidden_operation`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("f363e5e5-1d31-5f01-b58d-300501e9c4ff"),
            value = 10.00
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
            payload = payload
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("scenario-is-active-false.yml")
    @ExpectedDataSet(
        "scenario-is-active-false.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `transfer - must decline with error declined_by_forbidden_operation - alt`() {
        val payload = generateTransactionDTO(
            userId = UUID.fromString("f363e5e5-1d31-5f01-b58d-300501e9c4ff"),
            value = 10.00
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
            payload = payload
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("initial-state.yml")
    @ExpectedDataSet(
        "initial-state.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `transfer - must decline with error declined_by_same_wallet_transfer`() {
        val payload = generateTransactionDTO(
            walletId = UUID.fromString("a9a03905-d962-592c-aad0-f7555d81fe59"),
            value = 10.00
        )
        val responseBody = makeRequest(
            path = "/a9a03905-d962-592c-aad0-f7555d81fe59",
            payload = payload,
            statusCode = HttpStatus.BAD_REQUEST
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

}